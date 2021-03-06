package Lecture_08;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;


/**
 * Lecture 8: A simple application that illustrates drawing Letters H and W using OpenGL primitives and a vertex array.
 *  
 * @author Denis Gracanin
 * @version 1
 */
public class HelloWorldJOGLGL2 extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "Hello World JOGL GL2 Version";
	private GLJPanel panel = null;
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private static final String VERTEX_SHADER =
					"attribute vec4 vPosition;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
					"}\n";
	private static final String FRAGMENT_SHADER =
					"uniform vec4 color;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_FragColor = color;\n" +
					"}\n";
	/**
	 * Stores the vertex data, 2 values per vertex:
	 * 1st value: x coordinate
	 * 2nd value: y coordinate
	 */
	private float vertexData[] = null;

	/**
	 * Used for Vertex Buffer Object (VBO).
	 */
	private IntBuffer intBuffer = null;

	/**
	 * Used for Vertex Array Object (VAO).
	 */
	private FloatBuffer floatBuffer = null;
	private int location;

	/**
	 * Creates an instance of <code>SelectiveDrawing</code> with the default title.
	 */
	public HelloWorldJOGLGL2() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SelectiveDrawing</code> with the customized title.
	 */
	public HelloWorldJOGLGL2(String title) {
		super(title);
		vertexData = new float[]{
				-0.75f,  0.5f,  // vertex 0
				-0.75f, -0.5f,  // vertex 1
				-0.25f,  0.5f,  // vertex 2
				-0.25f, -0.5f,  // vertex 3
				-0.75f,  0.0f,  // vertex 4
				-0.25f,  0.0f,  // vertex 5

				 0.15f,  0.5f,  // vertex 6
				 0.40f, -0.5f,  // vertex 7
				 0.50f,  0.0f,  // vertex 8
				 0.60f, -0.5f,  // vertex 9
				 0.85f,  0.5f,  // vertex 10
		};
		setLayout(new BorderLayout());
		panel = new GLJPanel(new GLCapabilities(GLProfile.getDefault())); 
		panel.addGLEventListener(this);
		add(panel, BorderLayout.CENTER);
	}

	/**
	 * The main method.
	 * Creates the frame (application window) object, sets the size and shows it.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		HelloWorldJOGLGL2 frame = new HelloWorldJOGLGL2();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);		
	}

	/**
	 * Overridden to draw the primitives in the drawable area.
	 * 
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Set the background color (white).
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		// Use the shader		
		gl.glUseProgram(shaderProgram);

		// Set the VBO.
		intBuffer = Buffers.newDirectIntBuffer(1);
		gl.glGenBuffers(1, intBuffer);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, intBuffer.get(0));

		// Set the VAO.
		floatBuffer = Buffers.newDirectFloatBuffer(vertexData);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexData.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL2.GL_STATIC_DRAW);

		// Use vertex data in the vertexData array as vPosition vertex shader variable.
		location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 2, GL2.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);

		// Set the foreground color (white).
		location = gl.glGetUniformLocation(shaderProgram, "color");
		gl.glUniform4f(location, 0.0f, 0.0f, 0.0f, 0.0f);

		// Draw letter H (the first six vertices as 3 lines)
		gl.glDrawArrays(GL2.GL_LINES, 0, 6);
		
		// Draw letter W (the next five vertices as a line strip)
		gl.glDrawArrays(GL2.GL_LINE_STRIP, 6, 5);
	}

	/**
	 * Detaches and deletes the created shaders and the shader program.
	 *
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void dispose(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glDetachShader(shaderProgram, vertexShader);
		gl.glDeleteShader(vertexShader);
		gl.glDetachShader(shaderProgram, fragmentShader);
		gl.glDeleteShader(fragmentShader);
		gl.glDeleteProgram(shaderProgram);
	}

	/**
	 * Creates the shader program from source.
	 *
	 * @param drawable OpenGL drawable.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		vertexShader = compile(gl, GL2.GL_VERTEX_SHADER, VERTEX_SHADER);
		fragmentShader = compile(gl, GL2.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
		shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vertexShader);
		gl.glAttachShader(shaderProgram, fragmentShader);
		gl.glLinkProgram(shaderProgram);
	}

	/**
	 * Overridden as an empty method.
	 *
	 * @param drawable OpenGL drawable.
	 * @param x The x coordinate of the lower left corner of the viewport rectangle, in pixels.
	 * @param y The y coordinate of the lower left corner of the viewport rectangle, in pixels.
	 * @param width The width of the viewport.
	 * @param height The height of the viewport.
	 */
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

	/**
	 * A utility method to create a shader
	 * 
	 * @param gl The OpenGL context.
	 * @param shaderType The type of the shader.
	 * @param program The string containing the program.
	 * @return The created shader.
	 */
	private int compile(GL2 gl, int shaderType, String program) {
		int shader = gl.glCreateShader(shaderType);
		String[] lines = new String[] { program };
		int[] lengths = new int[] { lines[0].length() };
		gl.glShaderSource(shader, lines.length, lines, lengths, 0);
		gl.glCompileShader(shader);
		int[] compiled = new int[1];
		gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			int[] logLength = new int[1];
			gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH, logLength, 0);
			byte[] log = new byte[logLength[0]];
			gl.glGetShaderInfoLog(shader, logLength[0], (int[])null, 0, log, 0);
			System.err.println("Error compiling the shader: " + new String(log));
			System.exit(1);
		}
		return shader;
	}

}