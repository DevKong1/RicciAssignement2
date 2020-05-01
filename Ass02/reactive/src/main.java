public class main {
	
	public static void main(String[] args) {
		
		String link = "https://it.wikipedia.org/wiki/COVID-19";
		int depth = 3;
		
		//SharedContext controller
		SharedContext context = SharedContext.getIstance();
		
		new linksFinder(context,link,depth).start();
	}
}
