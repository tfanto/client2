package com.fnt.role;

public class Role {

	private Long id;

	private String name;

	private Boolean module1authorized;

	private Boolean accessmodule2;

	@Override
	public String toString() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getModule1authorized() {
		return module1authorized;
	}

	public void setModule1authorized(Boolean module1authorized) {
		this.module1authorized = module1authorized;
	}

	public Boolean getAccessmodule2() {
		return accessmodule2;
	}

	public void setAccessmodule2(Boolean accessmodule2) {
		this.accessmodule2 = accessmodule2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessmodule2 == null) ? 0 : accessmodule2.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((module1authorized == null) ? 0 : module1authorized.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (accessmodule2 == null) {
			if (other.accessmodule2 != null)
				return false;
		} else if (!accessmodule2.equals(other.accessmodule2))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (module1authorized == null) {
			if (other.module1authorized != null)
				return false;
		} else if (!module1authorized.equals(other.module1authorized))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
