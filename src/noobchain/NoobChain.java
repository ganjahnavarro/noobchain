package noobchain;

public class NoobChain {

	public static void main(String[] args) {

		Block firstBlock = new Block("Hi im the first block", "0");
		System.out.println("Hash for block 1 : " + firstBlock.getHash());

		Block secondBlock = new Block("Yo im the second block", firstBlock.getHash());
		System.out.println("Hash for block 2 : " + secondBlock.getHash());

		Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash());
		System.out.println("Hash for block 3 : " + thirdBlock.getHash());

	}

}
