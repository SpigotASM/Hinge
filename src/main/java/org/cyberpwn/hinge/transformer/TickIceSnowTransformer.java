package org.cyberpwn.hinge.transformer;

import org.cyberpwn.classtweaker.ClassTweakerHost;
import org.cyberpwn.classtweaker.IClassTransformer;
import org.cyberpwn.glog.L;
import org.cyberpwn.hinge.asmutil.ASM;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TickIceSnowTransformer implements IClassTransformer
{
	public byte[] transform(String className, byte[] bytes)
	{
		ClassNode node = ASM.readNode(bytes);
		boolean found = false;

		searching: for(Object i : node.methods)
		{
			MethodNode m = (MethodNode) i;

			for(AbstractInsnNode j : m.instructions.toArray())
			{
				if(j.getOpcode() == Opcodes.GETFIELD)
				{
					FieldInsnNode f = (FieldInsnNode) j;

					if(f.owner.equals("net/minecraft/server/" + ClassTweakerHost.getVersion() + "/WorldServer") && f.desc.equals("Ljava/util/Random;") && f.name.equals("random"))
					{
						if(f.getNext().getOpcode() == Opcodes.BIPUSH)
						{
							IntInsnNode next = (IntInsnNode) f.getNext();

							if(next.operand == 16)
							{
								if(next.getNext().getOpcode() == Opcodes.INVOKEVIRTUAL)
								{
									if(f.getPrevious().getOpcode() == Opcodes.ALOAD)
									{
										VarInsnNode var = (VarInsnNode) f.getPrevious();

										if(var.var == 0)
										{
											next.operand = 2147483647;
											found = true;
											break searching;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if(found)
		{
			L.l("WE FOUND IT THO");
			return ASM.writeNode(node);
		}

		return bytes;
	}
}
