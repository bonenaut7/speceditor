package by.fxg.pilesos.decals;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class CameraAlphaGroupStrategy extends CameraGroupStrategy {
	private ShaderProgram alphaShader;
	
	public CameraAlphaGroupStrategy(Camera camera) {
		super(camera);
		this.createDefaultShader();
	}
	
	public CameraAlphaGroupStrategy(Camera camera, Comparator<Decal> sorter) {
		super(camera, sorter);
		this.createDefaultShader();
	}

	public void beforeGroups () {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		this.alphaShader.bind();
		this.alphaShader.setUniformMatrix("u_projectionViewMatrix", this.getCamera().combined);
		this.alphaShader.setUniformi("u_texture", 0);
	}

	private void createDefaultShader () {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "uniform mat4 u_projectionViewMatrix;\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
			+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
			+ "precision mediump float;\n" //
			+ "#endif\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
			+ "  if (gl_FragColor.a < 0.5) discard;\n" //
			+ "}";

		this.alphaShader = new ShaderProgram(vertexShader, fragmentShader);
		if (!this.alphaShader.isCompiled()) throw new IllegalArgumentException("couldn't compile shader: " + alphaShader.getLog());
	}


	public ShaderProgram getGroupShader (int group) { 
		return this.alphaShader;
	}

	public void dispose () {
		super.dispose();
		if (this.alphaShader != null) this.alphaShader.dispose();
	}
}
