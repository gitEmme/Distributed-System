package Server;
//jrun -cp Provider.jar Middleware.ServerMiddleware.Provider ipAddress robotName

import Middleware.ServerMiddleware.CSkeletonF;
import Middleware.ServerMiddleware.CSkeletonH;
import Middleware.ServerMiddleware.CSkeletonOC;
import Middleware.ServerMiddleware.CSkeletonS;
import Middleware.ServerMiddleware.CSkeletonV;

public class Provider {
	public static void main(String[] args) throws InterruptedException {
		//SkeletonFactory factory= new SkeletonFactory();
		String robotName= new String("Robot1");
		String server= new String("192.168.0.105");
		String broker= new String("192.168.0.105");
		
		/*
		String robotName= args[0];
		String server= args[1]);
		String broker= args[2];*/

		//constructor order robotName robotAddr, pi address
		
		CSkeletonV skV= new CSkeletonV(robotName,server,broker);
		Thread sV = new Thread(skV);
		sV.start();
	
		Thread.sleep(1000);
		CSkeletonOC skOC= new CSkeletonOC(robotName,server,broker);
		Thread sOC = new Thread(skOC);
		sOC.start();
		
		Thread.sleep(1000);
		
		CSkeletonH skH= new CSkeletonH(robotName,server,broker);
		Thread sH = new Thread(skH);
		sH.start();
		Thread.sleep(1000);
		
		CSkeletonS sST= new CSkeletonS(robotName,server,broker);
		Thread s = new Thread(sST);
		s.start();
		Thread.sleep(1000);
		
		CSkeletonF sF= new CSkeletonF(robotName,server,broker);
		Thread sf = new Thread(sF);
		sf.start();
		Thread.sleep(1000);
		
		
	}
		
}

/*
new Thread((Runnable) factory.getSkeleton(50004)).start();
Thread.sleep(2000);
new Thread((Runnable) factory.getSkeleton(50005)).start();
Thread.sleep(2000);
new Thread((Runnable) factory.getSkeleton(50006)).start();
*/