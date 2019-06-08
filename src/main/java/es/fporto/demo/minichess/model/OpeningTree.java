package es.fporto.demo.minichess.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;


@Entity
public class OpeningTree {
	
	@Id
	private String id;
	@ManyToOne
	@JoinColumn
	@LazyCollection(LazyCollectionOption.TRUE)
	private OpeningTree parent;
	private String move;
	private String name;
	@OneToMany(cascade=CascadeType.ALL)
	private Map<String,OpeningTree> children=new HashMap<String,OpeningTree>();
	
	public OpeningTree(String id, OpeningTree parent, String move, String name, Map<String, OpeningTree> children) {
		super();
		this.id = id;
		this.parent = parent;
		this.move = move;
		this.name = name;
		this.children = children;
	}
	
	public OpeningTree(String id, OpeningTree parent, String move, String name) {
		super();
		this.id = id;
		this.parent = parent;
		this.move = move;
		this.name = name;
	}
	
	public OpeningTree() {
		super();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public OpeningTree getParent() {
		return parent;
	}
	public void setParent(OpeningTree parent) {
		this.parent = parent;
	}
	public String getMove() {
		return move;
	}
	public void setMove(String move) {
		this.move = move;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, OpeningTree> getChildren() {
		return children;
	}
	public void setChildren(Map<String, OpeningTree> children) {
		this.children = children;
	}
	
	

	
}
