package example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;


// mvn clean package shade:shade
// [A Basic AWS Lambda Example With Java](https://www.baeldung.com/java-aws-lambda)
// https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html
public class Hello implements RequestStreamHandler{
    public Hello(){}

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("UTF-8"))));

        String json = null;
        JSONObject inputObject = null;
        try {
            json = IOUtils.toString(reader);
            inputObject = JSON.parseObject(json);    // 拿到json对象
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cmd = inputObject.getString("cmd");


        Process process = new ProcessBuilder().command(cmd.split(" ")).start();
        InputStream inputStream2 = process.getInputStream();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream2));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

//        String input = IOUtils.toString(inputStream, "UTF-8");
        outputStream.write(stringBuilder.toString().getBytes());    // 写出响应
    }
}
