package example;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.*;
import java.util.stream.Collectors;


// mvn clean package shade:shade
// [A Basic AWS Lambda Example With Java](https://www.baeldung.com/java-aws-lambda)
// https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html
public class TestWrite implements RequestStreamHandler{
    public TestWrite(){}

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String event = (new BufferedReader(new InputStreamReader(inputStream))).lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject inputJson = JSONObject.parseObject(event);

        String response = processRequest(inputJson, context);
        outputStream.write(response.getBytes());
    }

    private String processRequest(JSONObject inputJson, Context context) {
        String fileName = inputJson.getString("fileName");
        String content = inputJson.getString("content");
        String result = writeFile(fileName, content);

        return result;
    }

    public static void main(String[] args) {
        String str1 = "file1_hello";
        String content1 = "test content.";

        // 写文件
        writeFile(str1, content1);

        // 读文件
//        readFile(str1);
    }

    private static String writeFile(String fileName, String fileContent) {
        try {
            (new FileOutputStream(fileName)).write(fileContent.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "OK";
    }
}
