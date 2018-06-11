package cads.test.junit.gui;

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

import Middleware.ClientMiddleware.CStub;
import Middleware.ClientMiddleware.CStub1;
import Middleware.ClientMiddleware.Stub;

public class CaDSEVGUISwingTest implements IIDLCaDSEV3RMIMoveGripper, IIDLCaDSEV3RMIMoveHorizontal, IIDLCaDSEV3RMIMoveVertical, IIDLCaDSEV3RMIUltraSonic, ICaDSRMIConsumer {
    static CaDSRobotGUISwing gui;
    String currentService;
    CStub clientStub;
    CStub1 clientStub1;
    int currentV=0;
    int currentH=0;
    int stateGripper=0;
    synchronized public void waithere() {
        try {
            
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Added Service.");
            gui.addService("TestService3");
            
            TimeUnit.SECONDS.sleep(5);
            System.out.println("removed Service.");
            gui.removeService("TestService3");
            
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Added Service.");
            gui.addService("TestService4");
            
            TimeUnit.SECONDS.sleep(5);
            System.out.println("removed Service.");
            gui.removeService("TestService4");
            
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Added Service.");
            gui.addService("TestService5");
            
            
            
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class SwingGUI implements Runnable {
        CaDSEVGUISwingTest c;

        public SwingGUI(CaDSEVGUISwingTest _c) {
            c = _c;
        }

        @Override
        public void run() {
            try {
                gui = new CaDSRobotGUISwing(c, c, c, c, c);
                gui.addService("TestService1");
                gui.addService("TestService2");
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        System.out.println("New Observer");
        observer.addService("Service 1");
        observer.addService("Service 2");
        observer.setChoosenService("Service 2", -1, -1, false);
    }

    @Override
    public void update(String comboBoxText) {
        System.out.println("Combo Box updated " + comboBoxText);
        this.currentService=comboBoxText;
        switch( this.currentService) {
        case "TestService1":
        	 clientStub= new CStub("client1",currentService);
        	 clientStub.registerClient("localhost");
        	 break;
        case "TestService2":
        	clientStub1= new CStub1("client1",currentService);
            clientStub1.registerClient("localhost");
        }
        
    }

    public int moveVerticalToPercent(int transactionID, int percent) throws Exception {
        System.out.println("Call to move vertical -  TID: " + transactionID + " degree " + percent);
        if(getCurrentVerticalPercent()==0) {
        	currentV=clientStub.moveVertical(percent,"up");
        }else{
        	if(percent>=currentV) {
        		currentV=clientStub.moveVertical(percent+currentV,"up");
        	}else {
        		currentV=clientStub.moveVertical(currentV-percent,"down");
        	}
        	
        }
        return currentV;
    }

    @Override
    public int getCurrentVerticalPercent() throws Exception {
        return currentV;
    }

    @Override
    public int moveHorizontalToPercent(int transactionID, int percent) throws Exception {
        System.out.println("Call to move horizontal -  TID: " + transactionID + " degree " + percent);
        if(getCurrentHorizontalPercent()==0) {
        	currentH=clientStub.moveHorizontal(percent,"left");
        }else{
        	if(percent>=currentH) {
        		currentH=clientStub.moveHorizontal(percent-currentH,"left");
        	}else {
        		currentH=clientStub.moveHorizontal(currentH-percent,"right");
        	}
        	
        }
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
        stateGripper=clientStub1.grabRelease("close");
        return stateGripper;
    }

    @Override
    public int openGripper(int transactionID) throws Exception {
        System.out.println("open.... TID: " + transactionID);
        stateGripper=clientStub1.grabRelease("open");
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
