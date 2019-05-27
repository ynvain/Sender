import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//sends messages to the exchange
public class Sender extends Application
{

    private final String EXCHANGE_NAME = "logs";
    private  String path = "1.txt";
    private ListView listView;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Sender");

        listView = new ListView();
        VBox vBox = new VBox(listView);

        Scene scene = new Scene(vBox, 300, 120);
        primaryStage.setScene(scene);
        primaryStage.show();


        try{
            Send();
        }catch (Exception e){System.out.print("Sorry, something is wrong!" + e);}




    }
    public static void main(String[] args) {
        launch(args);
    }

    //send messages from file to the exchange
    public void Send() throws Exception
    {
        String message;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        List<String> messageList = downloadFromFile(path);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");


            for(int i = 0; i < messageList.size();i++)
            {
                message = messageList.get(i);
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                listView.getItems().add(message + " ----SENT");
                Thread.sleep(1000);

            }


        }

    }

    //reads input form file
    public List<String> downloadFromFile(String path)
    {
        List<String> messageList = new ArrayList<String>();

        try
        {
            FileInputStream fstream_school = new FileInputStream(path);
            DataInputStream data_input = new DataInputStream(fstream_school);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
            String str_line;

            while ((str_line = buffer.readLine()) != null)
            {
                str_line = str_line.trim();
                if ((str_line.length()!=0))
                {
                    messageList.add(str_line);
                }
            }
        }
        catch (IOException e){
            System.out.print("Sorry, something is wrong!" + e);
        }

        return messageList;

    }
}
