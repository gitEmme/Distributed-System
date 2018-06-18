package Middleware;

import org.freedesktop.dbus.bin.CreateInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Server.ActionServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonCompiler {
	//port 50001 reserved for the coordinator
	private static int skPort=50003;
	private static int stPort=50010;
	private static int coordinatorPort=50002;
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub and skeleton 
			String filejson1=new String("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/IDLOpenClose.json");
			String filejson2=new String("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/IDLMoveVertical.json");
			String filejson3=new String("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/IDLMoveHorizontal.json");
			String filejson4=new String("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/IDLStopMove.json");
            
			String interfacciaScritta2=JsonCompiler.writeInterface(filejson1);
			System.out.println("Interface generated!");
			String interfacciaScritta3=JsonCompiler.writeInterface(filejson2);
			System.out.println("Interface generated!");
			String interfacciaScritta4=JsonCompiler.writeInterface(filejson3);
			System.out.println("Interface generated!");
			String interfacciaScritta5=JsonCompiler.writeInterface(filejson4);
			System.out.println("Interface generated!");
			
			JsonCompiler.createStub(filejson1,"CStubOC");
			System.out.println("Stub generated!");
			JsonCompiler.createStub(filejson2,"CStubV");
			System.out.println("Stub generated!");
			JsonCompiler.createStub(filejson3,"CStubH");
			System.out.println("Stub generated!");
			JsonCompiler.createStub(filejson4,"CStubS");
			System.out.println("Stub generated!");

			JsonCompiler.createSkeleton(filejson1,"CSkeletonOC");
			System.out.println("Skeleton generated!");
			JsonCompiler.createSkeleton(filejson2,"CSkeletonV");
			System.out.println("Skeleton generated!");
			JsonCompiler.createSkeleton(filejson3,"CSkeletonH");
			System.out.println("Skeleton generated!");
			JsonCompiler.createSkeleton(filejson4,"CSkeletonS");
			System.out.println("Skeleton generated!");
    }
	
	private static String writeInterface(String file) throws IOException {
		 String Function=JsonCompiler.getNameInterfaccia(file);
			HashMap<String,CProcedure> methods=JsonCompiler.fromJson(file);
         StringBuffer interfaccia= new StringBuffer();
         interfaccia.append("package Middleware;\n\n");
         interfaccia.append("public interface ");
         interfaccia.append(Function);
         interfaccia.append("{\n\t");
         for (String mName : methods.keySet()) {
         	String firma=JsonCompiler.createSignature(mName, methods.get(mName));
         	interfaccia.append(firma);
         	interfaccia.append(";\n\t");
         }
         interfaccia.append("}");
         System.out.println(interfaccia.toString());
         JsonCompiler.writeToFile("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/",Function, interfaccia.toString());
         return interfaccia.toString();
	}
	
	private static String getNameInterfaccia(String file) {
		JSONParser parser = new JSONParser();
		String ServiceName=new String();
		try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            ServiceName = (String) jsonObject.get("ServiceName");
			}catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
		return ServiceName;
	}
	
	private static String getServerName(String file) {
		JSONParser parser = new JSONParser();
		String Function=new String();
		try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject.size());
            Function= (String) jsonObject.get("Function");
            //System.out.println(ServiceName);
			}catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
		return Function;
	}
	
	private static HashMap<String,CProcedure> fromJson(String file){
		JSONParser parser = new JSONParser();
		HashMap<String,CProcedure> methods= new HashMap();
		ArrayList<String> mNames = new ArrayList();
		try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject.size());
            String ServiceName = (String) jsonObject.get("ServiceName");
            //System.out.println(ServiceName);
            String Function= (String) jsonObject.get("Function");
            JSONArray functions = (JSONArray) jsonObject.get("Functions");
            Iterator<JSONObject> functionIterator = functions.iterator();
            while (functionIterator.hasNext()) {
	        	JSONObject function= functionIterator.next();
	            String methodName= (String) function.get("name");
	            String returnType= (String) function.get("returnType");
	            mNames.add(methodName);
	            JSONArray parameters = (JSONArray) function.get("parameters");
	            Iterator<JSONObject> parametersIterator = parameters.iterator();
	            CProcedure procedure= new CProcedure(methodName, returnType);
	            procedure.setInterfaceName(Function);
	            procedure.setServiceName(ServiceName);
	            while(parametersIterator.hasNext()) {
	            	JSONObject params= parametersIterator.next();
                	String paramName= (String) params.get("name");
                	int paramPosition= Math.toIntExact((long) params.get("position"));
                	String paramType= (String) params.get("type");
                	CParameter param=new CParameter(paramName,paramType,paramPosition);
                	procedure.AddParam(param);
                methods.put(methodName, procedure);
	            }
	        }      
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return methods;
		
	}
	
	public static String getParamSig(CProcedure p) {
		StringBuffer paramSig= new StringBuffer();
		paramSig.append("(String methodName,");
		for(int i=1;i<=p.GetParamsCount();i++) {
			if(i>=2) {
				paramSig.append(", ");
			}
			paramSig.append(p.getParam(i).getType());
			paramSig.append(" ");
			paramSig.append(p.getParam(i).getName());
		}
		paramSig.append(")");
		return paramSig.toString();
	}

	public static void createStub(String file,String className) throws IOException {
		HashMap<String,CProcedure> methods=JsonCompiler.fromJson(file);
        StringBuffer stub= new StringBuffer();
        stub.append("package Middleware.ClientMiddleware;\n\n");      
        stub.append("import org.json.simple.JSONArray;\n");
        stub.append("import org.json.simple.JSONObject;\n");
        stub.append("import Middleware.*;\n");
        stub.append("public class "+className+" implements Runnable, "+JsonCompiler.getNameInterfaccia(file)+" {\n\t");
        stub.append("private Connection network=new Connection();\n\t");
        stub.append("private JSONObject message;\n\t");
        stub.append("private static int counter=1;\n\t");
        stub.append("private String brokerAddr= new String(\"localhost\");\n\t");
        stub.append("private int brokerPort=50001;\n\t");
        stub.append("private String clientName;\n\t");
        stub.append("private String serviceName;\n\t");
        stub.append("private Thread move;\n\t");
        stub.append("private boolean running= false;\n\t");
        stub.append("private int result;\n\t");
  
        stub.append("public "+className+"(String clientName,String serviceName) {\n\t\t");
		stub.append("this.clientName=clientName;\n\t\t");
		stub.append("this.serviceName=serviceName;\n\t");
		stub.append("}\n\t");
        
        for (String mName : methods.keySet()) {
        	String ovverride=JsonCompiler.marshallToJson(methods.get(mName));
        	stub.append(ovverride);
        
        }
        stub.append("public void run() {\n\t\t");
        stub.append("running=true;\n\t\t");
        stub.append("}\n\t");
        
        stub.append("\n}");
        stPort++;
        System.out.println(stub.toString());
        JsonCompiler.writeToFile("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/ClientMiddleware/",className, stub.toString());
		
	}
	
	public static String marshallToJson(CProcedure p) {
		StringBuffer marshall= new StringBuffer();
        //marshall.append("\n@Override\n");
		marshall.append("\n");
        marshall.append("public ");
        marshall.append(createSignature(p.getName(), p));
        marshall.append(" {\n\t");
		marshall.append("move= new Thread(\""+p.getName()+"\") {\n\t\t");
		marshall.append("public void run() {\n\t\t");
		marshall.append("message=new JSONObject();\n\t\t");
		marshall.append("JSONObject header=new JSONObject();\n\t\t");
		marshall.append("header.put(\"sourceName\",clientName);\n\t\t");
		marshall.append("header.put(\"destName\", serviceName);\n\t\t");
		marshall.append("JSONObject body=new JSONObject();\n\t\t");
		marshall.append("JSONArray params=new JSONArray();\n\t\t");
		int pNum=1;
		String messageID=new String();
		for(int i=1;i<=p.GetParamsCount();i++) {
			marshall.append("JSONObject param"+Integer.toString(pNum)+"=new JSONObject();\n\t\t");
			if(i==1) {
				messageID=p.getParam(i).getName();
			}
			
			if(p.getParam(i).getType().equals("int")) {
				marshall.append("param"+Integer.toString(pNum)+".put(\"name\","+"Integer.toString("+p.getParam(i).getName()+"));\n\t\t");
			}else {
				marshall.append("param"+Integer.toString(pNum)+".put(\"name\","+p.getParam(i).getName()+");\n\t\t");				
			}
			marshall.append("param"+Integer.toString(pNum)+".put(\"position\",\""+Integer.toString(p.getParam(i).getPosition())+"\");\n\t\t");
			marshall.append("param"+Integer.toString(pNum)+".put(\"type\",\""+p.getParam(i).getType()+"\");\n\t\t");
			marshall.append("params.add(param"+Integer.toString(pNum)+");\n\t\t");
			pNum++;
		}
		marshall.append("header.put(\"messageID\", Integer.toString("+messageID+"));\n\t\t");
		marshall.append("message.put(\"header\", header);\n\t\t");
		marshall.append("body.put(\"methodName\",\""+p.getName()+"\");\n\t\t");
		marshall.append("body.put(\"parameters\",params);\n\t\t");
		marshall.append("body.put(\"returnType\",\""+p.getReturnType()+"\");\n\t\t");
		marshall.append("message.put(\"body\", body);\n\t\t");
		marshall.append("System.out.println(message.toJSONString());\n\t\t");
		marshall.append("counter++;\n\t\t");	
		marshall.append("network=new Connection();\n\t\t");
		marshall.append("network.sendTo(message,brokerAddr,brokerPort);\n\t\t");
		marshall.append("}\n\t\t");
		marshall.append("};\n\t");
		marshall.append("move.start();\n\t");
		marshall.append("return this.result;\n\t}\n");
		
		
		System.out.println(marshall.toString());
		return marshall.toString();
		
	}
	
	public static void createSkeleton(String file, String className) throws IOException {
		HashMap<String,CProcedure> methods=JsonCompiler.fromJson(file);
        StringBuffer sk= new StringBuffer();
        String serverName=JsonCompiler.getServerName(file);
		sk.append("package Middleware.ServerMiddleware;\n");
		sk.append("import Middleware.*;\n");
		sk.append("import java.util.Iterator;\n");
		sk.append("import org.json.simple.JSONArray;\n");
		sk.append("import org.json.simple.JSONObject;\n");
		sk.append("import Server."+serverName+";\n\n");
		sk.append("public class "+className+" implements Runnable, "+JsonCompiler.getNameInterfaccia(file)+" {\n\t");
		String getServerName=new String();
		String getServerPort=new String();
		if(className.equals("CSkeletonH")) {
			getServerName=new String("getServerNameH()");
			getServerPort=new String("getServerPortH()");
		}
		if(className.equals("CSkeletonV")) {
			getServerName=new String("getServerNameV()");
			getServerPort=new String("getServerPortV()");
		}
		if(className.equals("CSkeletonOC")) {
			getServerName=new String("getServerNameOC()");
			getServerPort=new String("getServerPortOC()");
		}
		if(className.equals("CSkeletonS")) {
			getServerName=new String("getServerNameS()");
			getServerPort=new String("getServerPortS()");
		}
		sk.append("private Connection network;\n\t");
		sk.append(serverName+" action="+serverName+".getInstance();\n\t");
		sk.append("private int port;\n\t");
		sk.append("private int brokerPort=50001;\n\t");
		sk.append("private String brokerAddr= new String(\"localhost\");\n\n\t");
		sk.append("private String serverName= new String();\n\n\t");
		sk.append("private String serverIP= new String();\n\n\t");
		
		sk.append("public "+className+"() {\n\t\t");
		sk.append("this.port=action."+getServerPort+";\n\t");
		sk.append("}\n\t\n");
		
		sk.append("public "+className+"(String serverName,String serverIP) {\n\t\t");
		sk.append("this.port=action.getServerPortS();\n\t\t");
			sk.append("this.serverName=serverName;\n\t\t");
			sk.append("this.serverIP=serverIP;\n\t\t");
		sk.append("}\n\t");
		
		sk.append("public int execute(CProcedure p) {\n\t\t");
		sk.append("int result=0;\n\t\t");
		sk.append("switch (p.getName()) {\n\t\t\t");
		for (String mName : methods.keySet()) {
			sk.append("case \""+mName+"\":\n\t\t\t\t");
			CProcedure p=methods.get(mName);
			sk.append("result= "+mName+"(");
			for(int i=1;i<=p.GetParamsCount();i++) {
				//StringBuffer call= new StringBuffer();
				//if(i>2) {
					//sk.append(", ");
				//}
				if(p.getParam(i).getType().equals("int")) {
					sk.append("(int)p.getParam("+i+").getValue(p.getParam("+i+").getName())");
				}else {
					sk.append("(String)p.getParam("+i+").getValue(p.getParam("+i+").getName())");
				}
				if(i!=p.GetParamsCount()) {
					sk.append(", ");
					}else {
						sk.append(");\n\t\t\t\t");
					}
			}
			//sk.append(");\n\t\t\t\t");
			sk.append("break;\n\t\t\t");
		}
		sk.append("}\n\t\t\t");
		sk.append("return result;\n\t\t\t");
		sk.append("}\n\n\t");
	
	 //this part is just to copied as it is from Stub
	sk.append("public CEnvelope unmarshall() {\n\t\t");
	
	sk.append("network=new Connection();\n\t\t");
	sk.append("JSONObject received=(JSONObject) network.recvObjFrom(this.port,false);\n\t\t");
	sk.append("CEnvelope env= new CEnvelope();\n\t\t");
	sk.append("CHeader h= new CHeader();\n\t\t");
	sk.append("JSONObject header= (JSONObject) received.get(\"header\");\n\t\t");
	sk.append("h.setMessageID((String)header.get(\"messageID\"));\n\t\t");
	sk.append("h.setSourceName((String) header.get(\"sourceName\"));\n\t\t");
	sk.append("h.setDestName((String)header.get(\"destName\"));\n\t\t");

	sk.append("env.setHeader(h);\n\t\t");
	sk.append("JSONObject body=(JSONObject) received.get(\"body\");\n\t\t");
	sk.append("String methodName=(String) body.get(\"methodName\");\n\t\t");
	sk.append("String returnType= (String) body.get(\"returnType\");\n\t\t");
	sk.append("JSONArray params = (JSONArray) body.get(\"parameters\");\n\t\t");
	sk.append("CProcedure procedure= new CProcedure(methodName, returnType);\n\t\t");
	sk.append("Iterator<JSONObject> paramsIterator = params.iterator();\n\t\t");
	sk.append("while (paramsIterator.hasNext()) {\n\t\t\t");
	sk.append("JSONObject param= paramsIterator.next();\n\t\t\t");
	sk.append("String type = (String) param.get(\"type\");\n\t\t\t");
	sk.append("String name = (String) param.get(\"name\");\n\t\t\t");
	sk.append("int position= Integer.parseInt((String)param.get(\"position\"));\n\t\t\t");
	sk.append("CParameter unmPar = new CParameter(name,type,position);\n\t\t\t");
	sk.append("procedure.AddParam(unmPar);\n\t\t\t");
	sk.append("}\n\t\t");
	sk.append("env.setProcedure(procedure);\n\t\t");
	sk.append("System.out.println(body.toJSONString());\n\t\t");
	sk.append("return env;\n\t\t");
	sk.append("}\n\n\t");
	
	sk.append("public void run() {\n\t");
		// TODO Auto-generated method stub
	sk.append("registerService();\n\t");
	sk.append("while(true) {\n\t\t");
	sk.append("CEnvelope envelope=unmarshall();\n\t\t");
	sk.append("CHeader head=envelope.getHeader();\n\t\t");
	sk.append("CProcedure invoked=envelope.getProcedure();\n\t\t");
	sk.append("int p=execute(invoked);\n\t\t");
	//sk.append("System.out.println(\"executed!\");\n\t\t");
	
	sk.append("network=new Connection();\n\t\t");
	sk.append("JSONObject body=new JSONObject();\n\t\t");
	sk.append("JSONArray params=new JSONArray();\n\t\t");
	sk.append("JSONObject header= new JSONObject();\n\t\t");
	sk.append("JSONObject env= new JSONObject();\n\t\t");
	sk.append("header.put(\"sourceName\",head.getDestName());\n\t\t");
	sk.append("header.put(\"destName\",head.getSourceName());\n\t\t");
	sk.append("header.put(\"messageID\", head.getMessageID());\n\t\t");
	sk.append("body.put(\"methodName\", invoked.getName());\n\t\t");
	sk.append("body.put(\"parameters\", params);\n\t\t");
	sk.append("body.put(\"returnType\", invoked.getReturnType());\n\t\t");
	sk.append("env.put(\"header\", header);\n\t\t");
	sk.append("env.put(\"body\", body);\n\t\t");
	sk.append("env.put(\"result\", p);\n\t\t");
	sk.append("network.sendTo(env, brokerAddr, brokerPort);\n\t\t");
	sk.append("}\n\t");
	sk.append("}\n\n\t");
	//this part is to finish
	for (String mName : methods.keySet()) {
		CProcedure procedura=methods.get(mName);
		String sig=JsonCompiler.createSignature(mName, procedura);
		sk.append("public "+sig+" {\n\t\t");
		sk.append(procedura.getReturnType()+" p = action."+procedura.getName()+"(");
		for(int i=1;i<=procedura.GetParamsCount();i++) {
			//StringBuffer call= new StringBuffer();
			sk.append(procedura.getParam(i).getName());
			if(i!=procedura.GetParamsCount()) {
				sk.append(", ");
				}else {
					sk.append(");\n\t\t");
				}
		}
		sk.append("return p;\n\t}\n\n\t");	
	}
	
	sk.append("public void registerService() {\n\t\t");
	sk.append("network=new Connection();\n\t\t");
	sk.append("JSONObject env=new JSONObject();\n\t\t");
	sk.append("JSONObject header=new JSONObject();\n\t\t");
	sk.append("JSONObject body=new JSONObject();\n\t\t");
	sk.append("JSONObject result=new JSONObject();\n\t\t");
	sk.append("JSONArray params=new JSONArray();\n\t\t");
	sk.append("JSONObject param1=new JSONObject();\n\t\t");
	sk.append("JSONObject param2=new JSONObject();\n\t\t");
	sk.append("JSONObject param3=new JSONObject();\n\t\t");
	sk.append("header.put(\"sourceName\", action."+getServerName+");\n\t\t");
	sk.append("header.put(\"destName\", \"broker\");\n\t\t");
	sk.append("header.put(\"messageID\",\"registerMe\");\n\t\t");
	sk.append("body.put(\"methodName\", \"registerServer\");\n\t\t");
	sk.append("param1.put(\"name\", action."+getServerName+");\n\t\t");
	sk.append("param1.put(\"type\", \"String\");\n\t\t");
	sk.append("param1.put(\"position\", Integer.toString(1));\n\t\t");
	sk.append("param2.put(\"name\", action.getServerAddress());\n\t\t");
	sk.append("param2.put(\"type\", \"String\");\n\t\t");
	sk.append("param2.put(\"position\", Integer.toString(2));\n\t\t");
	sk.append("param3.put(\"name\", Integer.toString(action."+getServerPort+"));\n\t\t");
	sk.append("param3.put(\"type\", \"String\");\n\t\t");
	sk.append("param3.put(\"position\", Integer.toString(3));\n\t\t");
	sk.append("params.add(param1);\n\t\t");
	sk.append("params.add(param2);\n\t\t");
	sk.append("params.add(param3);\n\t\t");
	sk.append("body.put(\"parameters\", params);\n\t\t");
	sk.append("body.put(\"returnType\", \"String\");\n\t\t");
	sk.append("env.put(\"header\", header);\n\t\t");
	sk.append("env.put(\"body\", body);\n\t\t");
	sk.append("env.put(\"result\", result);\n\t\t");

	sk.append("int tries=5;\n\t\t");
	sk.append("boolean receivedResponse= false;\n\t\t");
	sk.append("boolean sent=false;\n\t\t");
	sk.append("do{\n\t\t\t");
	sk.append("network.sendTo(env, brokerAddr, brokerPort);\n\t\t\t");
	sk.append("sent = true;\n\t\t\t");
	sk.append("JSONObject received=(JSONObject) network.recvObjFrom(action."+getServerPort+",false);\n\t\t\t");
	sk.append("if (received!=null) {\n\t\t\t");			
	sk.append("receivedResponse=true;\n\t\t\t\t");
	sk.append("System.out.println(received.toJSONString());\n\t\t\t");
	sk.append("}else{\n\t\t\t");
	sk.append("tries --;\n\t\t\t\t");
	sk.append("System.out.println(\"Timed out: \"+  tries + \" tries left\");\n\t\t\t\t");
	sk.append("}\n\t\t\t");
	sk.append("}while(((!receivedResponse)&& tries!= 0) && (!sent));\n\t\t");
	sk.append("}\n\t");
	//sk.append("}\n\t");

	sk.append("public static void main(String[] args) {\n\t\t");
	sk.append(className+" sk= new "+className+"();\n\t\t");
	sk.append("Thread s = new Thread(sk);\n\t\t");
	sk.append("s.start();\n\t\t");
	sk.append("}\n\t}");

	skPort++;
	System.out.println(sk.toString());
    JsonCompiler.writeToFile("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/ServerMiddleware/",className, sk.toString());
	}
	
	
	
	private static String createSignature(String mName,CProcedure p) {
		StringBuffer paramsBuffer = new StringBuffer();
		for(int i=1;i<=p.GetParamsCount();i++) {
			if(i>=2) {
				paramsBuffer.append(", ");
			}
			paramsBuffer.append(p.getParam(i).getType());
			paramsBuffer.append(" ");
			paramsBuffer.append(p.getParam(i).getName());
		}
		StringBuffer signature= new StringBuffer();
		signature.append(p.getReturnType());
		signature.append(" ");
		signature.append(p.getName());
		signature.append("(");
		signature.append(paramsBuffer.toString());
		signature.append(")");
		//System.out.println(signature.toString());
		return signature.toString();
		
	}
	
	 private static void writeToFile(String path, String objectName, String classString) throws IOException {
	        String fileName;
	        //"/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/"
	        fileName = path + objectName + ".java";
	        PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName)));
	        writer.print(classString);
	        writer.flush();
	        writer.close();
	    }

    private static String readEntirefile(String fileName) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));

        String line = "";
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }
        reader.close();
        String jsonText = buffer.toString();
        return jsonText;
    }

}

