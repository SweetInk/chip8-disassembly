package online.githuboy.cd;

import java.io.*;

/**
 * Disassembly `CHIP8` Game Rom to assembly code.
 *
 * @author suchu
 * @since 2018/8/23 14:56
 */
public class Converter {

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
                return "SET Vx, " + "0x00" + Integer.toHexString(opcode & 0x00FF);
            case 0x4000:
                return "SNE Vx, " + "0x00" + Integer.toHexString(opcode & 0x00FF);
            case 0x5000:
                return "SE Vx,Vy";
            case 0x6000:
                return "LD Vx, " + "0x00" + Integer.toHexString(opcode & 0xFF);
            case 0x7000:
                return "ADD Vx, " + "0x00" + Integer.toHexString(opcode & 0xFF);
            case 0x8000:
                switch (opcode & 0x0f) {
                    case 0x0:
                        return "LD Vx, Vy";
                    case 0x1:
                        return "OR Vx, Vy";
                    case 0x2:
                        return "AND Vx, Vy";
                    case 0x3:
                        return "XOR Vx, Vy";
                    case 0x4:
                        return "ADD Vx, Vy";
                    case 0x5:
                        return "SUB Vx, Vy";
                    case 0x6:
                        return "SHR Vx, {,Vy}";
                    case 0x7:
                        return "SUBN Vx, Vy";
                    case 0xE:
                        return "SHL,Vx {,Vy}";
                }
                ;
            case 0x9000:
                return "SNE Vx, Vy";
            case 0xA000:
                return "LD I, " + operand;
            case 0xB000:
                return "JP V0, " + operand;
            case 0xC000:
                return "RND Vx, " + "0x00" + Integer.toHexString(opcode & 0xFF);
            case 0xD000:
                return "DRW Vx, Vy, " + "0x000" + Integer.toHexString(opcode & 0x0F);
            case 0xE000:
                switch (opcode & 0xFF) {
                    case 0x9E:
                        return "SKP Vx";
                    case 0xA1:
                        return "SKNP Vx";
                }
            case 0xF000:
                switch (opcode & 0xFF) {
                    case 0x07:
                        return "LD Vx, DT";
                    case 0x0A:
                        return "LD Vx, K";
                    case 0x15:
                        return "LD DT, Vx";
                    case 0x18:
                        return "LD ST, Vx";
                    case 0x1E:
                        return "ADD I, Vx";
                    case 0x29:
                        return "LD F, Vx";
                    case 0x33:
                        return "LD B, Vx";
                    case 0x55:
                        return "LD [I], Vx";
                    case 0x65:
                        return "LD Vx, [I]";
                }

        }
        return "NOOP";
    }
}
