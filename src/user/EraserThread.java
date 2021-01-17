package user;

class EraserThread implements Runnable {
	private boolean stop;

	public EraserThread(String prompt) {
		System.out.print(prompt);
	}

	public void run() {
		stop = true;
		while (stop) {
			System.out.print("\b*");
			try {
				Thread.sleep(25);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	public void stopMasking() {
		this.stop = false;
	}
}