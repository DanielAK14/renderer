
public class AmbientLight extends Light{
	
	public AmbientLight(ColorType _c) {
		color = _c;
	}
	
	public ColorType applyLight(Material mat) {
		return new ColorType(mat.ka.r*color.r,mat.ka.g*color.g,mat.ka.b*color.b);
	}
}
