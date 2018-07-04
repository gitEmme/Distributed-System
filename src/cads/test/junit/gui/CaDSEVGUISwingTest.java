package cads.test.junit.gui;

import java.util.LinkedList;

/*rendi la gui oserver degli stub: della coda che contiene i risultati ; notifica la gui quando un risultato arriva*/

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.cads.ev3.gui.ICaDSRobotGUIUpdater;
import org.cads.ev3.gui.swing.CaDSRobotGUISwing;
import org.cads.ev3.rmi.consumer.ICaDSRMIConsumer;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIMoveGripper;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIMoveHorizontal;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIMoveVertical;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIUltraSonic;
import org.junit.Test;

import Broker.Coordinator;
import Middleware.CResult;
import Middleware.ClientMiddleware.CStubH;
import Middleware.ClientMiddleware.CStubOC;
import Middleware.ClientMiddleware.CStubV;

public class CaDSEVGUISwingTest implements IIDLCaDSEV3RMIMoveGripper, IIDLCaDSEV3RMIMoveHorizontal, IIDLCaDSEV3RMIMoveVertical, IIDLCaDSEV3RMIUltraSonic, ICaDSRMIConsumer {
    static CaDSRobotGUISwing gui;
    String currentService;
    GuiController stub;
    int currentV=0;
    int currentH=0;
    int stateGripper=0;
    private LinkedList<String> availableRobots= new LinkedList();
    private LinkedList<String> currentRobots= new LinkedList();
  	private Timer timer = new Timer();
    private Thread f,l,r,d;
    private static Logger LOG = Logger.getLogger(CaDSEVGUISwingTest.class.getName());
    
    synchronized public void waithere() {
    	while(true) {
    	try {           
            
            this.wait();
    	
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    	}
    }

    private class SwingGUI implements Runnable {
        CaDSEVGUISwingTest c;
        

        public SwingGUI(CaDSEVGUISwingTest _c) {
            c = _c;
            stub=new GuiController("client2","172.16.1.100",50002,"172.16.1.62");
        	
        }
        

        @Override
        public void run() {
        	
        	try {
                gui = new CaDSRobotGUISwing(c, c, c, c, c);
            	//stub.registerClient();
                stub.registerClient();
                getList();
            	refresh();
                feedBack();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
  
    public void registerAgain() {
    	d=new Thread() {
    		public void run() {
    			while(true) {
	    			LOG.info("REGISTEEEEER!");      
	    			stub.registerClient();
	    	        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA"+availableRobots.toString());
	    	        try {
	    	        	Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			}
    		};
    	};
    	d.start();
    }
    public void feedBack() {
    	f=new Thread() {
    		public void run() {
    			while(true) {
    				LOG.info("FEEDBACK!");  
    				if(currentService!=null) {
		    	        CResult fb=stub.getResults(currentService);
		    	        LOG.info(fb.getResultH()+"  "+fb.getResultV());
		    	        gui.setHorizontalProgressbar(fb.getResultH());
		    	        gui.setVerticalProgressbar(fb.getResultV());
    				}
	    	        try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		};
    	};
    	f.start();
    }
    
    public void getList() {
    	l=new Thread() {
    		public void run() {
    			while(true) {
	    			LOG.info("ROBOOOOT!");      
	    	        availableRobots=stub.getRobotList();
	    	        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA"+availableRobots.toString());
	    	        try {
	    	        	Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			}
    		};
    	};
    	l.start();
    }
    
    public void refresh() {
    	r=new Thread() {
    		public void run() {
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                for(String r: availableRobots) {
                	if(!currentRobots.contains(r)) {
                		gui.addService(r);
                	    }
                	}
                for(String r: currentRobots) {
                	if(!availableRobots.contains(r)) {
                		gui.removeService(r);
                	    }
                	}
                currentRobots=availableRobots;
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               
    		while(true) {
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                for(String r: availableRobots) {
                	if(!currentRobots.contains(r)) {
                		gui.addService(r);
                	    }
                	}
                for(String r: currentRobots) {
                	if(!availableRobots.contains(r)) {
                		gui.removeService(r);
                	    }
                	}
                currentRobots=availableRobots;
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            
	    			}
    		};
    	};
    	r.start();
    }
   

    @Test
    public void test() {
        SwingUtilities.invokeLater(new SwingGUI(this));
        waithere();
        // fail("Not yet implemented");
    }

    @Override
    public void register(ICaDSRobotGUIUpdater observer) {
    	
    	for(String r: availableRobots) {
            observer.addService(r);
        }
    }

    @Override
    public void update(String comboBoxText) {
        //System.out.println("Combo Box updated " + comboBoxText);
        this.currentService=comboBoxText;
        stub.setCurrentServer(currentService);     
        
    }

    public int moveVerticalToPercent(int transactionID, int percent) throws Exception {
        LOG.info("Call to move vertical -  TID: " + transactionID + " degree " + percent);
        stub.moveVertical(transactionID, percent);       
        return currentV;
    }

    
    public void requestResult() {
    	stub.getResults(currentService);
    }
    
    @Override
    public int getCurrentVerticalPercent() throws Exception {
        return currentV;
    }

    @Override
    public int moveHorizontalToPercent(int transactionID, int percent) throws Exception {
        LOG.info("Call to move horizontal -  TID: " + transactionID + " degree " + percent);
        stub.moveHorizontal(transactionID, percent);
        return currentH;
    }

    @Override
    public int stop(int transactionID) throws Exception {
    	LOG.info("Stop movement.... TID: " + transactionID);
        return 0;
    }

    @Override
    public int getCurrentHorizontalPercent() throws Exception {
        return currentH;
    }

    @Override
    public int closeGripper(int transactionID) throws Exception {
    	LOG.info("Close.... TID: " + transactionID);
        stateGripper=stub.grabRelease(transactionID, "close");
        return stateGripper;
    }

    @Override
    public int openGripper(int transactionID) throws Exception {
    	LOG.info("open.... TID: " + transactionID);
        stateGripper=stub.grabRelease(transactionID, "open");
        return stateGripper;
    }

    @Override
    public int isGripperClosed() throws Exception {
        return stateGripper;
    }

    @Override
    public int isUltraSonicOccupied() throws Exception {
        return 0;
    }

}
