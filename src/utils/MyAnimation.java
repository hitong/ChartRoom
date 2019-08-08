package utils;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

public class MyAnimation {
	public static void showOut(Parent parent) {
		FadeTransition ft = new FadeTransition(Duration.millis(500), parent);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
	}
	
	public static void printMeg(String msg,TextArea textArea){
	    final Animation animation = new Transition() {
	        {
	            setCycleDuration(Duration.millis(1000));
	        }
	        protected void interpolate(double frac) {
	            final int length = msg.length();
	            final int n = Math.round(length * (float) frac);
	            textArea.setText(msg.substring(0, n));
	        }
	    };
	    animation.play();
	}
}
