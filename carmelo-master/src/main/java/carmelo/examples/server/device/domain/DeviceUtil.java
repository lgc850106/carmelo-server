package carmelo.examples.server.device.domain;

public class DeviceUtil {
	
	//工具方法，把long拆成两个int
	public static int[] longToInt(long in){
		int[] out = new int[2];
		out[0] = (int) (in & 0x00000000ffffffffL);//低32位
		out[1] = (int) (in >> 32);//高32位
		return out;
	}
	
	//工具方法，把两个int合并成一个long
	public static long intToLong(int[] in) {
		long out=0;

		if(in.length != 2) return out;
        long l1 = (in[1] & 0x000000ffffffffL) << 32;//高32位
        long l2 = in[0] & 0x00000000ffffffffL;//低32位
        out = l1 | l2;
		return out;
	}

}
