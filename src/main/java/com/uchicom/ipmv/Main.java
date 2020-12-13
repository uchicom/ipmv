package com.uchicom.ipmv;

import java.awt.Font;

import javax.swing.SwingUtilities;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.uchicom.ipmv.window.IpmFrame;

/**
 * ipmライブラリを利用したサンプルプログラム
 * 
 * @author Shigeki.Uchiyama
 *
 */
public class Main implements GLEventListener {

	public static void main(String[] args) {
		if (args.length == 0) {
			SwingUtilities.invokeLater(() -> {
				IpmFrame frame = new IpmFrame();
				frame.setVisible(true);
			});
		} else {
			new Main().newt();
		}
	}

	Animator animator;
	int width = 300;
	int height = 300;

	public void newt() {
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));// (2)
		GLWindow glWindow = GLWindow.create(caps); // (3)
		glWindow.setTitle("First demo (Newt)"); // (4)
		glWindow.setSize(300, 300); // (5)

		glWindow.addWindowListener(new WindowAdapter() { // (6)
			@Override
			public void windowDestroyed(WindowEvent evt) {
				System.exit(0);
			}
		});
		glWindow.addGLEventListener(this); // (7)

		animator = new Animator(); // (8)
		animator.add(glWindow);
		animator.start();
		glWindow.setVisible(true); // (10)
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();// 追加
		// ウィンドウを青く塗りつぶす
		gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);// 追加
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	long cnt = 0;

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();// 追加
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);// 追加
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2f(-0.9f, -0.9f);
		gl.glVertex2f(0.9f, -0.9f);
		gl.glVertex2f(0.9f, 0.9f);
		gl.glVertex2f(-0.9f, 0.9f);
		gl.glEnd();

		cnt++;
		// X軸回転
		gl.glRotatef(1.0f, 1.0f, 0.0f, 0.0f);
		// Y軸回転
		gl.glRotatef(1.5f, 0.0f, 1.0f, 0.0f);

		// 文字列を描画
		Font font = new Font("MSゴシック", java.awt.Font.PLAIN, 10);
		TextRenderer tr = new TextRenderer(font, true, true);
		tr.beginRendering(width, height);
		tr.setColor(1f, 1f, 0.5f, 1.0f);
		for (int i = 0; i < cnt && i < 20; i++) {
			tr.draw("テキスト漢字OK" + i, 20, 20 + (i * 10));
		}
	
		tr.endRendering();

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		if (animator != null)
			animator.stop();
	}

}
