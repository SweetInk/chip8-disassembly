package online.githuboy.cd;

import java.io.*;

/**
 * Disassembly `CHIP8` Game Rom to assembly code.
 *
 * @author suchu
 * @since 2018/8/23 14:56
 */
public class Converter {

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
            System.out.print("0x" + Integer.toHexString(opcode) + " " + "-> " + toASM(opcode) + "\n");
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
        String operand = "0x0" + Integer.toHexString(opcode & 0x0FFF);
        byte regVx = (byte) ((opcode & 0x0F00) >> 8);
        byte regVy = (byte) ((opcode & 0x00F0) >> 4);
        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode & 0x00FF) {
                    case 0xE0:
                        return "CLS";
                    case 0xEE:
                        return "RET";
                }
                return "SYS " + operand;
            case 0x1000:
                return "JP " + operand;
            case 0x2000:
                return "CALL " + operand;
            case 0x3000:
                return "SET " + V_REG[regVx] + ", " + "0x00" + Integer.toHexString(opcode & 0x00FF);
            case 0x4000:
                return "SNE " + V_REG[regVx] + ", " + "0x00" + Integer.toHexString(opcode & 0x00FF);
            case 0x5000:
                return "SE " + V_REG[regVx] + "," + V_REG[regVy] + "";
            case 0x6000:
                return "LD " + V_REG[regVx] + ", " + "0x00" + Integer.toHexString(opcode & 0xFF);
            case 0x7000:
                return "ADD " + V_REG[regVx] + ", " + "0x00" + Integer.toHexString(opcode & 0xFF);
            case 0x8000:
                switch (opcode & 0x0f) {
                    case 0x0:
                        return "LD " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0x1:
                        return "OR " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0x2:
                        return "AND " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0x3:
                        return "XOR " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0x4:
                        return "ADD " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0x5:
                        return "SUB " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0x6:
                        return "SHR " + V_REG[regVx] + ", {,"+V_REG[regVy]+"}";
                    case 0x7:
                        return "SUBN " + V_REG[regVx] + ", " + V_REG[regVy] + "";
                    case 0xE:
                        return "SHL," + V_REG[regVx] + " {,"+V_REG[regVy]+"}";
                }
                ;
            case 0x9000:
                return "SNE " + V_REG[regVx] + ", " + V_REG[regVy] + "";
            case 0xA000:
                return "LD I, " + operand;
            case 0xB000:
                return "JP V0, " + operand;
            case 0xC000:
                return "RND " + V_REG[regVx] + ", " + "0x00" + Integer.toHexString(opcode & 0xFF);
            case 0xD000:
                return "DRW " + V_REG[regVx] + ", " + V_REG[regVy] + ", " + "0x000" + Integer.toHexString(opcode & 0x0F);
            case 0xE000:
                switch (opcode & 0xFF) {
                    case 0x9E:
                        return "SKP " + V_REG[regVx] + "";
                    case 0xA1:
                        return "SKNP " + V_REG[regVx] + "";
                }
            case 0xF000:
                switch (opcode & 0xFF) {
                    case 0x07:
                        return "LD " + V_REG[regVx] + ", DT";
                    case 0x0A:
                        return "LD " + V_REG[regVx] + ", K";
                    case 0x15:
                        return "LD DT, " + V_REG[regVx] + "";
                    case 0x18:
                        return "LD ST, " + V_REG[regVx] + "";
                    case 0x1E:
                        return "ADD I, " + V_REG[regVx] + "";
                    case 0x29:
                        return "LD F, " + V_REG[regVx] + "";
                    case 0x33:
                        return "LD B, " + V_REG[regVx] + "";
                    case 0x55:
                        return "LD [I], " + V_REG[regVx] + "";
                    case 0x65:
                        return "LD " + V_REG[regVx] + ", [I]";
                }

        }
        return "NOOP";
    }
}
