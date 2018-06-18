package Middleware.ServerMiddleware;
//jrun -cp Provider.jar Middleware.ServerMiddleware.Provider ipAddress robotName

public class Provider {
	public static void main(String[] args) throws InterruptedException {
		//SkeletonFactory factory= new SkeletonFactory();
		

		CSkeletonV skV= new CSkeletonV();
		Thread sV = new Thread(skV);
		sV.start();
	
		Thread.sleep(2000);
		CSkeletonOC skOC= new CSkeletonOC();
		Thread sOC = new Thread(skOC);
		sOC.start();
		
		Thread.sleep(2000);
		
		CSkeletonH skH= new CSkeletonH();
		Thread sH = new Thread(skH);
		sH.start();
		Thread.sleep(2000);
		
		CSkeletonS sST= new CSkeletonS();
		Thread s = new Thread(sST);
		s.start();
		Thread.sleep(2000);
	}
		
}

/*
new Thread((Runnable) factory.getSkeleton(50004)).start();
Thread.sleep(2000);
new Thread((Runnable) factory.getSkeleton(50005)).start();
Thread.sleep(2000);
new Thread((Runnable) factory.getSkeleton(50006)).start();
*/