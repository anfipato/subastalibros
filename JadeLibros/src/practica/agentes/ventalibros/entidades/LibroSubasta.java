package practica.agentes.ventalibros.entidades;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LibroSubasta implements Serializable {
	String titulo;

	float precioInicial;

	float precioPuja;

	float precioMinimo;

	float precioCompra;

	String comprador;

	//Devuelve falso si se llego al precio mínimo
	public boolean ActualizarPuja(float valor) {
		this.precioPuja += valor;
		return this.precioPuja>this.precioMinimo;
	}
	
	//
	public boolean Comprado() {
		return !Anulado() && (this.comprador!=null && !"".equals(this.comprador));
	}
	
	public boolean Anulado() {
		return (this.comprador=="- ANULADO -");
	}
	
	public String getComprador() {
		return comprador;
	}

	public float getPrecioCompra() {
		return precioCompra;
	}

	public float getPrecioInicial() {
		
		return precioInicial;
	}

	public float getPrecioMinimo() {
		return precioMinimo;
	}

	public float getPrecioPuja() {
		return precioPuja;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setComprador(String comprador) {
		this.comprador = comprador;
	}

	public void setPrecioCompra(float precioCompra) {
		this.precioCompra = precioCompra;
	}
	public void setPrecioInicial(float precioInicial) {
		this.precioPuja = precioInicial;
		this.precioInicial = precioInicial;
	}
	public void setPrecioMinimo(float precioMinimo) {
		this.precioMinimo = precioMinimo;
	}
	public void setPrecioPuja(float precioPuja) {
		this.precioPuja = precioPuja;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
