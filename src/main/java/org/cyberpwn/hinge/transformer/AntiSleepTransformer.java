package org.cyberpwn.hinge.transformer;

import org.cyberpwn.classtweaker.IClassTransformer;
import org.cyberpwn.hinge.asmutil.ASM;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AntiSleepTransformer implements IClassTransformer
{
	public byte[] transform(String className, byte[] bytes)
	{
		ClassNode node = ASM.readNode(bytes);

		searching: for(Object i : node.methods)
		{
			MethodNode m = (MethodNode) i;

			if(m.name.equals("main"))
			{
				for(AbstractInsnNode j : m.instructions.toArray())
				{
					if(j.getOpcode() == Opcodes.GETSTATIC)
					{
						FieldInsnNode f = (FieldInsnNode) j;

						if(f.owner.equals("java/util/concurrent/TimeUnit") && f.desc.equals("Ljava/util/concurrent/TimeUnit;") && f.name.equals("SECONDS"))
						{
							f.name = "MILLISECONDS";

							break searching;
						}
					}
				}
			}
		}

		return ASM.writeNode(node);
	}
}
