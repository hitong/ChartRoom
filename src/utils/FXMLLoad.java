package utils;

import java.io.IOException;

import client.AddFriendControl;
import client.ChatViewControl;
import client.ContextControl;
import client.LaunchClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.Friend;

public class FXMLLoad {
	public static <V extends InversionControl<LaunchClient>> Scene load(String sourse, LaunchClient main){
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(LaunchClient.class.getResource(sourse));
		try {
			Parent parent = loader.load();
			Scene scene = new Scene(parent);
			V c = loader.getController();
			c.setMain(main);
			main.setViewControl(c);
			return scene;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Parent load(String sourse, ChatViewControl main, Friend friend){
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(LaunchClient.class.getResource(sourse));
		try {
			Parent parent = loader.load();
			if(friend != null){
				ContextControl c = loader.getController();
				c.setMain(main);
				c.setFriend(friend);
				main.addChat(friend.getUserId().getValue(), c);
			} else {
				AddFriendControl c = loader.getController();
				c.setMain(main);
				main.setAddCtl(c);
			}
			return parent;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}	
