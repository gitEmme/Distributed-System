package Middleware;

import org.freedesktop.dbus.bin.CreateInterface;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonCompiler {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub and skeleton 
			String fileJson=new String("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/IDLMoveAround.json");
            String interfacciaScritta=JsonCompiler.writeInterface(fileJson);
			System.out.println("Interface generated!");
			JsonCompiler.createStub(fileJson);
			System.out.println("Stub generated!");
			JsonCompiler.createSkeleton(fileJson);
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
            //System.out.println(jsonObject.size());
            ServiceName = (String) jsonObject.get("ServiceName");
            //System.out.println(ServiceName);
            //Function= (String) jsonObject.get("Function");
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

	public static void createStub(String file) throws IOException {
		HashMap<String,CProcedure> methods=JsonCompiler.fromJson(file);
        StringBuffer stub= new StringBuffer();
        stub.append("package Middleware.ClientMiddleware;\n\n");      
        stub.append("import org.json.simple.JSONArray;\n");
        stub.append("import org.json.simple.JSONObject;\n");
        stub.append("import Middleware.*;\n");
        stub.append("public class CStub implements "+JsonCompiler.getNameInterfaccia(file)+" {\n\t");
        stub.append("private Connection network=new Connection();\n\t");
        stub.append("private JSONObject message;\n\t");
        stub.append("private static int counter=1;\n\t");
        stub.append("private int stubPort=0;\n\t");
        stub.append("private String stubAddress;\n\t");
        stub.append("private int port;\n\t");
        stub.append("private String address;\n\t\n\t");
        stub.append("public CStub(int port, String address, int stubPort, String stubAddress) {\n\t\t");
        stub.append("this.port=port;\n\t\t");
        stub.append("this.address=address;\n\t\t");
        stub.append("this.stubAddress=stubAddress;\n\t\t");
        stub.append("this.stubPort=stubPort;\n\t");
        stub.append("}\n\t");
        for (String mName : methods.keySet()) {
        	String ovverride=JsonCompiler.marshallToJson(methods.get(mName));
        	stub.append(ovverride);
        
        }
        stub.append("\n}");
        System.out.println(stub.toString());
        JsonCompiler.writeToFile("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/ClientMiddleware/","CStub", stub.toString());
		
	}
	
	public static void createSkeleton(String file) throws IOException {
		HashMap<String,CProcedure> methods=JsonCompiler.fromJson(file);
        StringBuffer sk= new StringBuffer();
        String serverName=JsonCompiler.getServerName(file);
		sk.append("package Middleware.ServerMiddleware;\n");
		sk.append("import Middleware.*;\n");
		sk.append("import java.util.Iterator;\n");
		sk.append("import org.json.simple.JSONArray;\n");
		sk.append("import org.json.simple.JSONObject;\n");
		sk.append("import Server."+serverName+";\n\n");
		sk.append("public class CSkeleton implements Runnable, "+JsonCompiler.getNameInterfaccia(file)+" {\n\t");
		sk.append("private Connection network;\n\t");
		sk.append(serverName+" action=new "+serverName+"();\n\t");
		sk.append("private int port;\n\t");
		sk.append("public CSkeleton(int port) {\n\t\t");
		sk.append("this.port=port;\n\t");
		sk.append("}\n\t\n");
		
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
	sk.append("JSONObject received=(JSONObject) network.recvObjFrom(this.port);\n\t\t");
	sk.append("CEnvelope env= new CEnvelope();\n\t\t");
	sk.append("CHeader h= new CHeader();\n\t\t");
	sk.append("JSONObject header= (JSONObject) received.get(\"header\");\n\t\t");
	sk.append("h.setProcedureID((String)header.get(\"procedureID\"));\n\t\t");
	sk.append("h.setStubAddress((String)header.get(\"stubAddress\"));\n\t\t");
	sk.append("h.setStubPort((Integer)header.get(\"stubPort\"));\n\t\t");
	sk.append("h.setServiceName((String)header.get(\"serviceName\"));\n\t\t");
	sk.append("h.setSourceName((String) header.get(\"sourceName\"));\n\t\t");
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

	//@Override
	sk.append("public void run() {\n\t");
		// TODO Auto-generated method stub
	sk.append("while(true) {\n\t\t");
	sk.append("CEnvelope envelope=unmarshall();\n\t\t");
	sk.append("CHeader head=envelope.getHeader();\n\t\t");
	sk.append("CProcedure invoked=envelope.getProcedure();\n\t\t");
		    //int p=execute(invoked.getName(),Integer.parseInt(invoked.getParam(1).getName()),invoked.getParam(2).getName());
	sk.append("int p=execute(invoked);\n\t\t");
	sk.append("System.out.println(\"executed!\");\n\t\t");
	sk.append("int stubPort=head.getStubPort();\n\t\t");
	sk.append("String stubAddr=head.getStubAddress();\n\t\t");
	sk.append("network=new Connection();\n\t\t");
	sk.append("JSONObject result=new JSONObject();\n\t\t");
	sk.append("result.put(\"result\", p);\n\t\t");
	sk.append("network.sendTo(result, stubAddr, stubPort);\n\t\t");
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
	sk.append("public static void main(String[] args) {\n\t\t");
	sk.append("Skeleton sk= new Skeleton(50001);\n\t\t");
	sk.append("Thread s = new Thread(sk);\n\t\t");
	sk.append("s.start();\n\t\t");
	sk.append("}\n\t}");
	
	System.out.println(sk.toString());
    JsonCompiler.writeToFile("/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/ServerMiddleware/","CSkeleton", sk.toString());
	
	/*
	@Override
	public int moveHorizontal(int integer, String string) {
		//action.setMove(string);
		int p =action.moveHorizontal(integer, string);
		return p;
	}

	@Override
	public int moveVertical(int integer, String string) {
		//action.setMove(string);
		int p=action.moveVertical(integer, string);
		return p;
	}
	
	

	}
	*/
	}
	
	public static String marshallToJson(CProcedure p) {
		StringBuffer marshall= new StringBuffer();
        //marshall.append("\n@Override\n");
		marshall.append("\n");
        marshall.append("public ");
        marshall.append(createSignature(p.getName(), p));
        marshall.append(" {\n\t");
		//public void marshall(String methodName,int integer,String string) {
		marshall.append("message=new JSONObject();\n\t");
		marshall.append("JSONObject header=new JSONObject();\n\t");
		marshall.append("header.put(\"serviceName\",\""+p.getServiceName()+"\");\n\t");
		marshall.append("header.put(\"source\",\"clientStub\");\n\t");
		marshall.append("header.put(\"id\",\""+p.getName()+"\"+Integer.toString(counter));\n\t");
		marshall.append("message.put(\"header\", header);\n\t");
		marshall.append("JSONObject body=new JSONObject();\n\t");
		marshall.append("JSONArray params=new JSONArray();\n\t");
		int pNum=1;
		for(int i=1;i<=p.GetParamsCount();i++) {
			marshall.append("JSONObject param"+Integer.toString(pNum)+"=new JSONObject();\n\t");
			if(p.getParam(i).getType().equals("int")) {
				marshall.append("param"+Integer.toString(pNum)+".put(\"name\","+"Integer.toString("+p.getParam(i).getName()+"));\n\t");
			}else {
				marshall.append("param"+Integer.toString(pNum)+".put(\"name\","+p.getParam(i).getName()+");\n\t");				
			}
			marshall.append("param"+Integer.toString(pNum)+".put(\"position\",\""+Integer.toString(p.getParam(i).getPosition())+"\");\n\t");
			marshall.append("param"+Integer.toString(pNum)+".put(\"type\",\""+p.getParam(i).getType()+"\");\n\t");
			marshall.append("params.add(param"+Integer.toString(pNum)+");\n\t");
			pNum++;
		}
		marshall.append("body.put(\"methodName\",\""+p.getName()+"\");\n\t");
		marshall.append("body.put(\"parameters\",params);\n\t");
		marshall.append("body.put(\"returnType\",\""+p.getReturnType()+"\");\n\t");
		marshall.append("message.put(\"body\", body);\n\t");
		marshall.append("System.out.println(message.toJSONString());\n\t");
		marshall.append("counter++;\n\t");	
		//marshall.append("sender.sendTo(message, \"localhost\", 50001);\n\t");
		marshall.append("network=new Connection();\n\t");
		marshall.append("network.sendTo(message,this.address,port);\n\t");
		marshall.append("JSONObject res= (JSONObject) network.recvObjFrom(this.stubPort);\n\t");
		marshall.append("int result=(int)res.get(\"result\");\n\t");
		if(p.getReturnType().equals("int")) {
			marshall.append("return result;\n");
		}else {
		}
		marshall.append("\t}\n");
		System.out.println(marshall.toString());
		return marshall.toString();
		
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

