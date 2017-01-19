package Presentacion;

import com.github.sarxos.webcam.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import org.slf4j.LoggerFactory;



public class server implements ThreadFactory, WebcamListener {

	private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(server.class) ;

	private static final String BOUNDARY = "mjpegframe";

    @Override
    public void webcamOpen(WebcamEvent we) {
        start();
    }

    @Override
    public void webcamClosed(WebcamEvent we) {
        stop();
    }

    @Override
    public void webcamDisposed(WebcamEvent we) {
    }

    @Override
    public void webcamImageObtained(WebcamEvent we) {
    }
    
	private class Acceptor implements Runnable {

		@Override
		public void run() {
			try {
				ServerSocket server = new ServerSocket(port);
				while (started.get()) {
					Socket socket = server.accept();
					LOG.info("New connection from {}", socket.getRemoteSocketAddress());
					executor.execute(new Connection(socket));
				}
			} catch (Exception e) {
				LOG.error("Cannot accept socket connection", e);
			}
		}
	}

	private class Connection implements Runnable {

		private Socket socket = null;

		public Connection(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			BufferedOutputStream bos = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream salida=null;
			try {
                                bos=new BufferedOutputStream(socket.getOutputStream());
                                salida=new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				LOG.error("Fatal I/O exception when creating socket streams", e);
				try {
					socket.close();
				} catch (IOException e1) {
					LOG.error("Canot close socket connection from " + socket.getRemoteSocketAddress(), e1);
				}
				return;
			}

			// stream

			try {
				socket.setSoTimeout(0);
				socket.setKeepAlive(false);
				socket.setTcpNoDelay(true);

				while (started.get()) {
					do {
						if (!webcam.isOpen() || socket.isInputShutdown() || socket.isClosed()) {
							bos.close();
							return;
						}
						baos.reset();
						image = webcam.getImage();
                                               
						ImageIO.write(image, "JPG", baos);
                                                salida.writeInt(baos.size());
						try {
							bos.write(baos.toByteArray());
							bos.flush();
						} catch (SocketException e) {
							LOG.error("Socket exception from " + socket.getRemoteSocketAddress(), e);
							bos.close();
							return;
						}
                                                Thread.sleep(delay);
					} while (started.get());
				}
			} catch (Exception e) {

				String message = e.getMessage();

				if (message != null) {
					if (message.startsWith("Software caused connection abort")) {
						LOG.info("User closed stream");
						return;
					}
					if (message.startsWith("Broken pipe")) {
						LOG.info("User connection broken");
						return;
					}
				}

				LOG.error("Error", e);

				try {
					bos.write("HTTP/1.0 501 Internal Server Error\r\n\r\n\r\n".getBytes());
				} catch (IOException e1) {
					LOG.error("Not ablte to write to output stream", e);
				}

			} finally {
				for (Closeable closeable : new Closeable[] { salida, bos, baos }) {
					try {
						closeable.close();
					} catch (IOException e) {
						LOG.error("Cannot close socket", e);
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					LOG.error("Cannot close socket", e);
				}
			}
		}
	}

	private Webcam webcam = null;
	private double fps = 0;
	private int number = 0;
	private int port = 0;
	private long last = -1;
	private long delay = -1;
	private BufferedImage image = null;
	private ExecutorService executor = Executors.newCachedThreadPool(this);
	private AtomicBoolean started = new AtomicBoolean(false);

	public server(int port, Webcam webcam, double fps, boolean start) {

		if (webcam == null) {
			throw new IllegalArgumentException("Webcam for streaming cannot be null");
		}

		this.port = port;
		this.webcam = webcam;
		this.fps = fps;
		this.delay = (long) (1000 / fps);

		if (start) {
			start();
		}
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, String.format("streamer-thread-%s", number++));
		thread.setUncaughtExceptionHandler(WebcamExceptionHandler.getInstance());
		thread.setDaemon(true);
		return thread;
	}

	public void start() {
		if (started.compareAndSet(false, true)) {
			webcam.addWebcamListener(this);
			webcam.open();
			executor.execute(new Acceptor());
		}
	}

	public void stop() {
		if (started.compareAndSet(true, false)) {
			executor.shutdown();
			webcam.removeWebcamListener(this);
			webcam.close();
		}
	}


	public double getFPS() {
		return fps;
	}

	public boolean isInitialized() {
		return started.get();
	}

	public int getPort() {
		return port;
	}

}
