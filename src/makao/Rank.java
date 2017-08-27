package makao;

public enum Rank {
	bl2("2"), bl3("3"), bl4("4"), bl5("5"), bl6("6"), bl7("7"), bl8("8"), 
	bl9("9"), bl10("10"), jack("J"), queen("Q"), king("K"), ace("A");
	
	private String sign;
	private Rank(String s){
		this.sign = s;
	}
	
	public String getSign(){
		return this.sign;
	}
}