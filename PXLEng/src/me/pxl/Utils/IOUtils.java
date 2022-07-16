package me.pxl.Utils;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.system.MemoryUtil.memSlice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;

public class IOUtils {
	
	public static String stream2string(InputStream in) {
		return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
			        .lines()
			        .collect(Collectors.joining("\n"));
	}
	@SuppressWarnings("rawtypes")
	public static InputStream getinfromclasspath(Class c,String name) {
		return c.getResourceAsStream(name);
	}
	
	//TODO replace readallbytes
	public static String stringfromFile(String resource) {
		try {
			return new String(Files.readAllBytes(Paths.get(resource)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String stringfromFile(Path resource) {
		try {
			return new String(Files.readAllBytes(resource));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	static LinkedList<Path> plist=new LinkedList<>();
	
	public static String includeget(Path p) {
		String s=get(p);
		plist.clear();
		return s;
		
	}
	
	private static String get(Path p) {
		StringBuilder strbuild=new StringBuilder();
		String src="\n"+IOUtils.stringfromFile(p);
		int i=-1;
		int lastconcat=0;
		while((i=src.indexOf("\n#include<",i))>=0){
			int begin=i+1;
			int end=src.indexOf(">",i);
			Path pa=p.getParent().resolve(src.substring(begin+9,end));
			if(plist.contains(pa)) {
				strbuild.append(src.substring(lastconcat, begin));
				lastconcat=end+1;
				i=end;
				continue;
			}
			plist.add(pa);
			strbuild.append(src.substring(lastconcat, begin))
					.append(get(plist.getLast()));
			lastconcat=end+1;
			i=end;
		}
		strbuild.append(src.substring(lastconcat));
		return strbuild.toString();
	}
	
	public static void write(String s, Path p) {
		try {
			Files.write(p, s.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static ByteBuffer fromInputStream(ByteBuffer b,InputStream is) throws IOException {
		while(is.available()>0)
			b.put((byte) is.read());
		return b;
	}
	public static ByteBuffer fromInputStream(InputStream is) throws IOException {
		return fromInputStream(BufferUtils.createByteBuffer(is.available()), is);
	}
	
	
	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        
        Path path = Paths.get(resource);
        
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                InputStream source = IOUtils.class.getClassLoader().getResourceAsStream(resource);
                ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
