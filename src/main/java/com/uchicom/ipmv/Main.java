package com.uchicom.ipmv;

import java.nio.IntBuffer;

import javax.swing.SwingUtilities;

import com.uchicom.ipmv.window.IpmFrame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.FontSmoothingType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * ipmライブラリを利用したサンプルプログラム
 * 
 * @author Shigeki.Uchiyama
 *
 */
public class Main extends Application {

	public static void main(String[] args) {
		if (args.length == 0) {
			SwingUtilities.invokeLater(() -> {
				IpmFrame frame = new IpmFrame();
				frame.setVisible(true);
			});
		} else {
			launch();
		}
	}

	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;

	WritableImage image = new WritableImage(WIDTH, HEIGHT);

	Canvas canvas = new Canvas(WIDTH, HEIGHT);
	GraphicsContext gc = canvas.getGraphicsContext2D();

	private boolean dragg;
	private boolean angle;
	private double tx;
	private double ty;
	private double tz;
	private double stageX;
	private double stageY;
	private double sx;
	private double sy;
	private double x;
	private double y;
	private double z;

	@Override
	public void start(Stage stage) {
		Group root = new Group();

		// オブジェクト設定
		Sphere sphere = new Sphere(20);
		Box myBox = new Box(WIDTH, HEIGHT, 1);

		// テクスチャ設定
		final PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(image);

		sphere.setMaterial(material);
		myBox.setMaterial(material);
		myBox.setScaleZ(0.5);
		myBox.setScaleY(0.5);
		myBox.setScaleZ(0.5);

		// root.getChildren().add(sphere);
		root.getChildren().add(myBox);

		Scene scene = new Scene(root, 320, 320, true, SceneAntialiasing.BALANCED);

		// カメラ3D
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setFieldOfView(80);
		camera.setTranslateZ(-100.001);
		camera.setTranslateY(10);
		camera.setTranslateX(10);
		scene.setCamera(camera);
		stage.setScene(scene);
		stage.setTitle("Hello, JavaFX 3D World!");
		stage.setOpacity(0.5);
		stage.setAlwaysOnTop(true);

		// 枠削除
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.getScene().setFill(null);
		stage.getScene().getRoot().setStyle("-fx-background-color: transparent");

		scene.setOnMousePressed(value -> {
			if (value.isControlDown()) {
				angle = true;
				x = value.getX();
				y = value.getY();
				z = value.getZ();
				tx = camera.getTranslateX();
				ty = camera.getTranslateY();
				tz = camera.getTranslateZ();
			} else {
				dragg = true;
				stageX = stage.getX();
				stageY = stage.getY();
				sx = value.getScreenX();
				sy = value.getScreenY();
				System.out.println("1x,y,z=" + x + "," + y + "," + z);
			}
		});
		scene.setOnMouseDragged(value -> {
			if (dragg) {

				double dx = value.getScreenX() - sx;
				double dy = value.getScreenY() - sy;
				stage.setX(stageX + dx);
				stage.setY(stageY + dy);
				System.out.println("2x,y,z=" + x + "," + y + "," + z);
			} else if (angle) {
				double dx = value.getScreenX() - sx;
				double dy = value.getScreenY() - sy;
				camera.setTranslateX(tx + dx / 10);
				camera.setTranslateY(ty + dy / 10);
//				camera.setTranslateZ(camera.getTranslateZ() + dz / 10);
			}
		});
		scene.setOnMouseReleased(value -> {
			dragg = false;
			System.out.println("setOnMouseReleased");
		});
		scene.setOnMouseDragExited(value -> {
			dragg = false;
			System.out.println("setOnMouseDragExited");
		});
		stage.show();
		// テクスチャが同的に変えられるかの確認スレッド
		Thread thread = new Thread(() -> {
			while (true) {
				PixelWriter writer = image.getPixelWriter();
				try {
					Thread.sleep(1000);
					// 描画スレッドはこれを使う。
					Platform.runLater(() -> {
						drawShapes(gc);
						WritableImage temp = canvas.snapshot(null, null);
						WritablePixelFormat<IntBuffer> pf = WritablePixelFormat.getIntArgbInstance();
						int[] img = new int[WIDTH * HEIGHT];
						temp.getPixelReader().getPixels(0, 0, WIDTH, HEIGHT, pf, img, 0, WIDTH);
						writer.setPixels(0, 0, WIDTH, HEIGHT, pf, img, 0, WIDTH);
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	int i = 0;

	/**
	 * テクスチャ描画確認
	 * 
	 * @param gc
	 */
	private void drawShapes(GraphicsContext gc) {
		// gc.setFontSmoothingType(FontSmoothingType.LCD);
		gc.setFill(Paint.valueOf("#FF0000"));
		gc.fillRect(0, 0, WIDTH, HEIGHT);
		gc.setFill(Paint.valueOf("#FFFFFF"));
		// gc.clearRect(0, 0, 100, 100);
		for (int j = 0; j < 10; j++) {
			//受信したメッセージを縦に流れるようにする。
		gc.fillText(String.valueOf(i++) + ":あいうえお", WIDTH / 2, HEIGHT / 2+j * 15);
		}
	}

}
