package example;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import jdk.nashorn.api.scripting.URLReader;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class TestRead implements RequestStreamHandler {


    private static String readFile(String fileUrl) {
        URL url;

        try {
            url = new URL(fileUrl);
            URLConnection con = url.openConnection();    // 打开连接
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());    // 读取输入流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bis));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


    // Ref: https://www.geeksforgeeks.org/how-to-access-private-field-and-method-using-reflection-in-java/
    private static String testURLReader(String url) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        jdk.nashorn.api.scripting.URLReader  urlReader = new URLReader(new URL(url));
        Method getReader = URLReader.class.getDeclaredMethod("getReader");    // 拿到getReader方法
        getReader.setAccessible(true);    // 将private方法设置为可访问
        Reader reader =  (Reader)getReader.invoke(urlReader);    // 调用getReader方法
        org.apache.commons.io.input.ReaderInputStream readerInputStream = new ReaderInputStream(reader, "UTF-8", 1024);

        org.apache.commons.io.input.BOMInputStream bomInputStream = new BOMInputStream(readerInputStream);
        org.apache.commons.io.ByteOrderMark boms = bomInputStream.getBOM();
        byte[] bytes = boms.getBytes();

        return new String(bytes);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String event = (new BufferedReader(new InputStreamReader(inputStream))).lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject inputJson = JSONObject.parseObject(event);

        String response = processRequest(inputJson, context);
        outputStream.write(response.getBytes());
    }

    private String processRequest(JSONObject inputJson, Context context) {
        String fileName = inputJson.getString("fileName");
        String result = readFile(fileName);

        return result;
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String str1 = "file:C:/repos/Hello/str1";
        String content1 = "test content.";

        // 读文件
//        readFile(str1);
        testURLReader(str1);
    }
}
