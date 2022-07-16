package me.pxl.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.pxl.Engine;
import me.pxl.Post.post.Bloom.Bloom;
import me.pxl.Post.post.Lightning.Lighting;
import me.pxl.Post.post.Tonemap.Tonemap;
import me.pxl.Serialize.Serialize;
import me.pxl.Serialize.SerializeationAdapter;

public class PostProcessor{
	public List<Post> posts;
	Post tonemap;
	public PostProcessor() {
		tonemap=new Tonemap();
		posts=new ArrayList<>();
		posts.add(new Lighting());
		posts.add(new Bloom());
	}
	
	public void post(Engine e) {
		posts.forEach((a)->a.process(e, e.getFinalBuffer().getAttachments()[0]));
		tonemap.process(e, e.getFinalBuffer().getAttachments()[0]);
	}
	
	
	public void serialize(SerializeationAdapter s) throws IOException {
		s.begin("PostProcess");
		Serialize.serializeclass(tonemap, s);
//		s.beginArray("Filters");
		for(Post p:posts)
			Serialize.serializeclass(p, s);
//		s.endArray("Filters");
		s.exit();
	}
	
	public void deserialize(SerializeationAdapter s) throws IOException {
		s.begin("PostProcess");
		tonemap=Serialize.deserializeclass(Post.class, s);
		s.beginArray("Filters");
		while(s.hasnext())
			posts.add(Serialize.deserializeclass(Post.class, s));
		s.endArray("Filters");
		s.exit();
	}
	
}
