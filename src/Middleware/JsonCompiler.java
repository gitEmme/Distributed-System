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
         JsonCompiler.writeToFile(Function, interfaccia.toString());
         return interfaccia.toString();
	}
	
	private static String getNameInterfaccia(String file) {
		JSONParser parser = new JSONParser();
		String Function=new String();
		try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject.size());
            String ServiceName = (String) jsonObject.get("ServiceName");
            //System.out.println(ServiceName);
            Function= (String) jsonObject.get("Function");
			}catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
		return Function;
	}
	
	private static String getNameService(String file) {
		JSONParser parser = new JSONParser();
		String ServiceName=new String();
		try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            //System.out.println(jsonObject.size());
            ServiceName = (String) jsonObject.get("ServiceName");
            //System.out.println(ServiceName);
			}catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
		return ServiceName;
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
        stub.append("package Middleware;\n\n");      
        stub.append("import org.json.simple.JSONArray;\n");
        stub.append("import org.json.simple.JSONObject;\n");
        stub.append("public class CStub implements "+JsonCompiler.getNameInterfaccia(file)+" {\n\t");
        stub.append("private Sender sender=new Sender();\n\t");
        stub.append("private Receiver receiver;\n\t");
        stub.append("private JSONObject message;\n\t");
        stub.append("private static int counter=1;\n\t");
        for (String mName : methods.keySet()) {
        	String ovverride=JsonCompiler.marshallToJson(methods.get(mName));
        	stub.append(ovverride);
        
        }
        stub.append("\n}");
        System.out.println(stub.toString());
        JsonCompiler.writeToFile("CStub", stub.toString());
		
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
		marshall.append("sender.sendTo(message, \"localhost\", 50001);\n\t");
		if(p.getReturnType().equals("int")) {
			marshall.append("return 1;\n");
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
	
	 private static void writeToFile(String objectName, String classString) throws IOException {
	        String fileName;
	        fileName = "/home/dude/VS/src/git/CaDSPracticalExamVS/src/Middleware/" + objectName + ".java";
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

