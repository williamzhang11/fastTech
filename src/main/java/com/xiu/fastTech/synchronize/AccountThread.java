package com.xiu.fastTech.synchronize;

public class AccountThread {

	public static void main(String[] args) {
		
		Account account = new Account("zhangsna", 10000);
		
		AccountOperator accountOperator = new AccountOperator(account);
		
		Thread threads[] = new Thread[5];
		
		for (int i = 0; i < 5; i ++) {
			   threads[i] = new Thread(accountOperator, "Thread" + i);
			   threads[i].start();
			}
		
		
	}
	
	static class Account{
		String name;
		float amount;
		
		public Account(String name, float amount) {
			this.name = name;
			this.amount = amount;
		}
		
		public void deposit(float amt) {
			
			amount +=amt;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//取钱
		   public  void withdraw(float amt) {
		      amount -= amt;
		      try {
		         Thread.sleep(100);
		      } catch (InterruptedException e) {
		         e.printStackTrace();
		      }
		   }
		 
		   public float getBalance() {
		      return amount;
		   }
	}
	
	static class AccountOperator implements Runnable{

		private Account account;
		
		public AccountOperator(Account account) {
			this.account = account;
		}
		
		public void run() {
			synchronized (account) {
				account.deposit(500);
				account.withdraw(500);
				 System.out.println(Thread.currentThread().getName() + ":" + account.getBalance());
			}
			
		}
		
	}
	
	
}
