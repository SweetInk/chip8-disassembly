package online.githuboy.cd;

/**
 * Chip8 instruction sets enumeration
 *
 * @author suchu
 * @since 2018/8/26 22:40
 */
public enum Opcode {
    SYS,//System call
    CLS,// Clear screen
    RET,//RET
    JP,//Address jump
    CALL,//call sub proc
    LD, //LD Vx,Vy
    ADD,// 7xkk - ADD Vx, byte | 8xy4 - ADD Vx, Vy
    OR,
    AND,
    SUB,
    XOR,
    SHR,
    SE,
    SNE,
    SHL,
    SUBN,
    SET,//SET Vx,
    RND,
    DRW,
    SKP,
    SKNP;

    public String code(String ...operands){
        if(operands.length==3){
            return  this.toString()+" "+operands[0]+","+" "+operands[1]+" "+","+ " "+operands[2];
        } else if (operands.length ==2){
            return this.toString()+" "+operands[0]+","+" "+operands[1];
        } else if (operands.length == 1) {
         return this.toString()+" "+ operands[0];
        }else if(operands.length==0){
            return this.toString();
        }else return "NOOP";
    }

    public static void main(String[] args) {
        System.out.println(Opcode.LD.code());
    }
}
