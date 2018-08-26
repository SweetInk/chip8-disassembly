package online.githuboy.cd;

import java.io.*;

import online.githuboy.cd.Opcode.*;

/**
 * Disassembly `CHIP8` Game Rom to assembly code.
 *
 * @author suchu
 * @since 2018/8/23 14:56
 */
public class Converter {

    private final static int VX_MASK = 0x0F00;
    private final static int VY_MASK = 0x00F0;
    private final static int ADDRESS_MASK = 0x0FFF;
    private final static int M_8BIT = 0x00FF;
    private final static int M_4BIT = 0x000F;
    private final static int OPCODE_MASK = 0xF000;


    /**
     * register sets
     */
    private final static String V_REG[] = {"V0", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "VA", "VB", "VC", "VD", "VE", "VF"};

    public static void main(String[] args) throws IOException {
        if (args.length <= 0) {
            System.out.println("No input file");
            return;
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("Invalid file path: " + args[0]);
            return;
        }
        InputStream inputStream = new FileInputStream(file);
        int len = 0;
        byte[] buffer = new byte[100];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        byte[] memory = bos.toByteArray();
        int pc = 0;
        while (pc + 1 < memory.length) {
            int b1 = memory[pc] & 0xFF;
            int b2 = memory[pc + 1] & 0xFF;
            int opcode = b1 << 8 | b2;
            System.out.print(toASM(opcode) + "\n");
            pc += 2;
        }
        if (pc < memory.length) {
            System.out.println("FINALLY:" + Integer.toHexString(memory[pc] & 0xFF));
        }
    }

    /**
     * convert hex of two byte to Assembly code
     *
     * @param opcode
     * @return
     */
    private static String toASM(int opcode) {
        String operand = "0x0" + Integer.toHexString(opcode & ADDRESS_MASK);
        byte regVx = (byte) ((opcode & VX_MASK) >> 8);
        byte regVy = (byte) ((opcode & VY_MASK) >> 4);
        String Vx = V_REG[regVx];
        String Vy = V_REG[regVy];
        switch (opcode & OPCODE_MASK) {
            case 0x0000:
                switch (opcode & M_8BIT) {
                    case 0xE0:
                        return Opcode.CLS.code();
                    case 0xEE:
                        return Opcode.RET.code();
                }
                return Opcode.SYS.code(operand);
            case 0x1000:
                return Opcode.JP.code(operand);
            case 0x2000:
                return Opcode.CALL.code(operand);
            case 0x3000:
                return Opcode.SET.code(Vx, "0x00" + Integer.toHexString(opcode & M_8BIT));
            case 0x4000:
                return Opcode.SNE.code(Vx, "0x00" + Integer.toHexString(opcode & M_8BIT));
            case 0x5000:
                return Opcode.SE.code(Vx, Vy);
            case 0x6000:
                return Opcode.LD.code(Vx, "0x00" + Integer.toHexString(opcode & M_8BIT));
            case 0x7000:
                return Opcode.ADD.code(Vx, "0x00" + Integer.toHexString(opcode & M_8BIT));
            case 0x8000:
                switch (opcode & 0x0f) {
                    case 0x0:
                        return Opcode.LD.code(Vx, Vy);
                    case 0x1:
                        return Opcode.OR.code(Vx, Vy);
                    case 0x2:
                        return Opcode.AND.code(Vx, Vy);
                    case 0x3:
                        return Opcode.XOR.code(Vx, Vy);
                    case 0x4:
                        return Opcode.ADD.code(Vx, Vy);
                    case 0x5:
                        return Opcode.SUB.code(Vx, Vy);
                    case 0x6:
                        return Opcode.SHR.code(Vx, "{," + Vy + "}");
                    case 0x7:
                        return Opcode.SUBN.code(Vx, Vy);
                    case 0xE:
                        return Opcode.SHL.code(Vx, "{," + Vy + "}");
                }
                ;
            case 0x9000:
                return Opcode.SNE.code(Vx, Vy);
            case 0xA000:
                return Opcode.LD.code("I", operand);
            case 0xB000:
                return Opcode.JP.code(V_REG[0], operand);
            case 0xC000:
                return Opcode.RND.code(Vx, "0x00" + Integer.toHexString(opcode & M_8BIT));
            case 0xD000:
                return Opcode.DRW.code(Vx, Vy, "0x000" + Integer.toHexString(opcode & M_4BIT));
            case 0xE000:
                switch (opcode & M_8BIT) {
                    case 0x9E:
                        return Opcode.SKP.code(Vx);
                    case 0xA1:
                        return Opcode.SKNP.code(Vx);
                }
            case 0xF000:
                switch (opcode & M_8BIT) {
                    case 0x07:
                        return Opcode.LD.code(Vx, "DT");
                    case 0x0A:
                        return Opcode.LD.code(Vx, "K");
                    case 0x15:
                        return Opcode.LD.code("DT", Vx);
                    case 0x18:
                        return Opcode.LD.code("ST", Vx);
                    case 0x1E:
                        return Opcode.AND.code("I", Vx);
                    case 0x29:
                        return Opcode.LD.code("F", Vx);
                    case 0x33:
                        return Opcode.LD.code("B", Vx);
                    case 0x55:
                        return Opcode.LD.code("[I]", Vx);
                    case 0x65:
                        return Opcode.LD.code(Vx, "[I]");
                }

        }
        return "NOOP";
    }
}
