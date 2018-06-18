package cads.test.junit.gui;

import java.util.LinkedList;

/*rendi la gui oserver degli stub: della coda che contiene i risultati ; notifica la gui quando un risultato arriva*/

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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
import Middleware.ClientMiddleware.Stub;

public class CaDSEVGUISwingTest implements IIDLCaDSEV3RMIMoveGripper, IIDLCaDSEV3RMIMoveHorizontal, IIDLCaDSEV3RMIMoveVertical, IIDLCaDSEV3RMIUltraSonic, ICaDSRMIConsumer {
    static CaDSRobotGUISwing gui;
    String currentService;
    //CStub clientStub;
    //CStubOC clientStubOC;
    //CStubV clientStubV;
    //CStubH clientStubH;
    Stub stub;
    int currentV=0;
    int currentH=0;
    int stateGripper=0;
    LinkedList<String> availableRobots= new LinkedList();
    LinkedList<String> currentRobots= new LinkedList();
    
    synchronized public void waithere() {
       
    	try {
    		
            TimeUnit.SECONDS.sleep(1);
            for(String r: currentRobots) {
                gui.removeService(r);
                System.out.println("removed Service. "+r);
            }
            currentRobots=availableRobots;
            TimeUnit.SECONDS.sleep(1);
            for(String r: availableRobots) {
                gui.addService(r);
                System.out.println("added Service. "+r);
            }
            
            
            
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    private class SwingGUI implements Runnable {
        CaDSEVGUISwingTest c;

        public SwingGUI(CaDSEVGUISwingTest _c) {
            c = _c;
            stub=new Stub("client1","localhost",50002);
        }
        

        @Override
        public void run() {
        	
        	try {
                gui = new CaDSRobotGUISwing(c, c, c, c, c);
            	stub.registerClient();
            	Timer timer = new Timer();
                timer.scheduleAtFixedRate(new RefreshRobotList(), 0, 10000);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private class RefreshRobotList extends TimerTask{
        
        private void getListRobots() {
               
         availableRobots=stub.getRobotList();
         System.out.println(availableRobots.toString());
         CResult fb=stub.getResults();
         gui.setHorizontalProgressbar(fb.getResultH());
         gui.setVerticalProgressbar(fb.getResultV());
         /*try {
                availableRobots=stub.getRobotList();
                   //assuming it takes 10 secs to complete the task
                   Thread.sleep(10000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }*/
           }
        
     @Override
     public void run() {
      // TODO Auto-generated method stub
      getListRobots();
     }
        
       }

    @Test
    public void test() {
        SwingUtilities.invokeLater(new SwingGUI(this));
        waithere();
        // fail("Not yet implemented");
    }

    @Override
    public void register(ICaDSRobotGUIUpdater observer) {
        /*System.out.println("New Observer");
        observer.addService("Service 1");
        observer.addService("Service 2");
        observer.addService("Service 3");
        observer.addService("Service 4");
        observer.setChoosenService("Service 2", -1, -1, false);
        */
    	//availableRobots=stub.getRobotList();
    	for(String r: availableRobots) {
            observer.addService(r);
        }
    	
    }

    @Override
    public void update(String comboBoxText) {
        System.out.println("Combo Box updated " + comboBoxText);
        this.currentService=comboBoxText;
        stub.setCurrentServer(currentService);     
        
    }

    public int moveVerticalToPercent(int transactionID, int percent) throws Exception {
        System.out.println("Call to move vertical -  TID: " + transactionID + " degree " + percent);
        stub.moveVertical(transactionID, percent);       
        return currentV;
    }

    
    public void requestResult() {
    	stub.getResults();
    }
    
    @Override
    public int getCurrentVerticalPercent() throws Exception {
        return currentV;
    }

    @Override
    public int moveHorizontalToPercent(int transactionID, int percent) throws Exception {
        System.out.println("Call to move horizontal -  TID: " + transactionID + " degree " + percent);
        stub.moveHorizontal(transactionID, percent);
        return currentH;
    }

    @Override
    public int stop(int transactionID) throws Exception {
        System.out.println("Stop movement.... TID: " + transactionID);
        return 0;
    }

    @Override
    public int getCurrentHorizontalPercent() throws Exception {
        return currentH;
    }

    @Override
    public int closeGripper(int transactionID) throws Exception {
        System.out.println("Close.... TID: " + transactionID);
        stateGripper=stub.grabRelease(transactionID, "close");
        return stateGripper;
    }

    @Override
    public int openGripper(int transactionID) throws Exception {
        System.out.println("open.... TID: " + transactionID);
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
