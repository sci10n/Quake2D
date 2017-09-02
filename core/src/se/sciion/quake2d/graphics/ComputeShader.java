package se.sciion.quake2d.graphics;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Disposable;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.GL43.glDispatchCompute;
import static org.lwjgl.opengl.GL45.glTextureParameteri;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

/**
 * 
 * @author sciion
 *	Wrapper for a compute shader which uses a texture as output buffer
 */
public class ComputeShader implements Disposable{

	private int computeShader;
	private int computeProgram;
	private Texture outputBuffer;
	public ComputeShader(){
		
	}
	
	public void init(String path, int width, int height){
		outputBuffer = new Texture(width, height, Format.RGBA8888);
		computeShader = glCreateShader(GL_COMPUTE_SHADER);
		
		FileHandle handle = Gdx.files.internal(path);

		String fileSource = handle.readString();
		glShaderSource(computeShader,fileSource);
		glCompileShader(computeShader);
		
		int compileStatus = glGetShaderi(computeShader, GL_COMPILE_STATUS);
		if(compileStatus == 0){
			String log = glGetShaderInfoLog(computeShader, 512);
			System.err.println("ERR:SHADER::COMPILATION in \n" + log);
			return;
		}
		computeProgram = glCreateProgram();
		glAttachShader(computeProgram, computeShader);
		glLinkProgram(computeProgram);
		
		int linkingStatus = glGetProgrami(computeProgram, GL_LINK_STATUS);
		if(linkingStatus == 0){
			String log = glGetProgramInfoLog(computeProgram, 512);
			System.err.println("ERR:SHADER::PROGRAM::LINKING in \n" + log);
			return;
		}
		
		glActiveTexture(GL_TEXTURE0);
		outputBuffer.bind();
		glTextureParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTextureParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTextureParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, outputBuffer.getWidth(), outputBuffer.getHeight(), 0,Format.toGlFormat(Format.RGBA8888) , GL_FLOAT, (ByteBuffer)null);
		glBindImageTexture(0, outputBuffer.getTextureObjectHandle(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
		glActiveTexture(0);
	}
	
	public void render(){
		outputBuffer.bind();
		glUseProgram(computeProgram);
		glDispatchCompute(outputBuffer.getWidth(), outputBuffer.getHeight(), 1);
		glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
		glActiveTexture(0);
	}
	
	public Texture getBuffer(){
		return outputBuffer;
	}
	
	@Override
	public void dispose() {
		if(outputBuffer != null)
			outputBuffer.dispose();
		glDeleteShader(computeShader);
		glDeleteProgram(computeProgram);
	}
	
	
}
