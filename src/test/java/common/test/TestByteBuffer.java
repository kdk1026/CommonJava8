package common.test;

import java.nio.ByteBuffer;

import common.util.date.Jsr310DateUtil;

public class TestByteBuffer {

	public static void main(String[] args) {
		String sDate = Jsr310DateUtil.Today.getTodayString("yyyyMMdd");
		
		String sMsg = "8400100"+sDate;
		byte[] bMsg = sMsg.getBytes();
		
		testArrayCopy(bMsg);
		System.out.println();
		testArrayCopy();
		
		System.out.println();
		testBuffer(bMsg);
		System.out.println();
		testBuffer();
	}
	
	public static void testArrayCopy() {
		String sDate = Jsr310DateUtil.Today.getTodayString("yyyyMMdd");
		
		byte[] bMsg = new byte[15];
		
		byte[] bMsgType = "84".getBytes();
		byte[] bMsgLen = String.format("%05d", 100).getBytes();
		byte[] bSysYmd = sDate.getBytes();
		
		System.arraycopy(bMsgType, 0, bMsg, 0, bMsgType.length);
		System.arraycopy(bMsgLen, 0, bMsg, bMsgType.length, bMsgLen.length);
		System.arraycopy(bSysYmd, 0, bMsg, bMsgType.length+bMsgLen.length, bSysYmd.length);
		
		System.out.println(String.format("bMsg :: [%s]", new String(bMsg)));
	}
	
	public static void testArrayCopy(byte[] bMsg) {
		byte[] bMsgType = new byte[2];
		byte[] bMsgLen = new byte[5];
		byte[] bSysYmd = new byte[8];
		
		System.arraycopy(bMsg, 0, bMsgType, 0, bMsgType.length);
		System.arraycopy(bMsg, bMsgType.length, bMsgLen, 0, bMsgLen.length);
		System.arraycopy(bMsg, bMsgType.length+bMsgLen.length, bSysYmd, 0, bSysYmd.length);
		
		System.out.println(String.format("bMsgType :: [%s]", new String(bMsgType)));
		System.out.println(String.format("bMsgLen :: [%s]", new String(bMsgLen)));
		System.out.println(String.format("bSysYmd :: [%s]", new String(bSysYmd)));
	}
	
	public static void testBuffer(byte[] bMsg) {
		ByteBuffer buffer = ByteBuffer.wrap(bMsg);
		
		buffer.limit(2);
		byte[] bMsgType = new byte[buffer.remaining()];
		buffer.get(bMsgType);
		
		buffer.limit(buffer.position() + 5);
		byte[] bMsgLen = new byte[buffer.remaining()];
		buffer.get(bMsgLen);
		
		buffer.limit(buffer.position() + 8);
		byte[] bSysYmd = new byte[buffer.remaining()];
		buffer.get(bSysYmd);
		
		buffer.clear();
		
		System.out.println(String.format("bMsgType :: [%s]", new String(bMsgType)));
		System.out.println(String.format("bMsgLen :: [%s]", new String(bMsgLen)));
		System.out.println(String.format("bSysYmd :: [%s]", new String(bSysYmd)));
	}
	
	public static void testBuffer() {
		String sDate = Jsr310DateUtil.Today.getTodayString("yyyyMMdd");
		
		ByteBuffer buffer = ByteBuffer.allocate(15);
		
		buffer.put("84".getBytes());
		buffer.put(String.format("%05d", 100).getBytes());
		buffer.put(sDate.getBytes());
		
		buffer.flip();
		
		System.out.println(String.format("bMsg :: [%s]", new String(buffer.array())));
	}
	
}
