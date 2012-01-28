package uy.globalgamejam.medusa;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uy.globalgamejam.medusa.Game;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.gemserk.commons.lwjgl.FocusableLwjglApplicationDelegate;
import com.gemserk.commons.utils.gdx.LwjglLibgdxLibraryUtils;

public class Applet extends java.applet.Applet {

	private static final long serialVersionUID = 6396112708370503447L;

	protected static final Logger logger = LoggerFactory.getLogger(Applet.class);

	private Canvas canvas;

	private LwjglApplication application;

	public void start() {

	}

	public void stop() {

	}

	public void destroy() {
		remove(canvas);
		super.destroy();
	}

	public void init() {

		GdxNativesLoader.disableNativesLoading = true;
		LwjglLibgdxLibraryUtils.loadLibgdxLibrary();

		try {
			setLayout(new BorderLayout());

			canvas = new Canvas() {
				public final void addNotify() {
					super.addNotify();

					Game game = new Game();

					application = new LwjglApplication(new FocusableLwjglApplicationDelegate(game), false, this) {
						public com.badlogic.gdx.Application.ApplicationType getType() {
							return ApplicationType.Applet;
						};
					};
				}

				public final void removeNotify() {
					application.stop();
					super.removeNotify();
				}
				
				{
					addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							requestFocus();
						}

						@Override
						public void mouseExited(MouseEvent e) {
							getParent().requestFocus();
						};
					});
				}
			};
			canvas.setSize(getWidth(), getHeight());
			add(canvas);
			canvas.setIgnoreRepaint(true);

			validate();
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display", e);
		}
	}
}
