/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author sergiorosadoferrer
 */
@Entity
@Table(name = "Comic")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comic.findAll", query = "SELECT c FROM Comic c")
    , @NamedQuery(name = "Comic.findByIdComic", query = "SELECT c FROM Comic c WHERE c.idComic = :idComic")
    , @NamedQuery(name = "Comic.findByNombre", query = "SELECT c FROM Comic c WHERE c.nombre = :nombre")
    , @NamedQuery(name = "Comic.findByDescripcion", query = "SELECT c FROM Comic c WHERE c.descripcion = :descripcion")
    , @NamedQuery(name = "Comic.findByFechaCreacion", query = "SELECT c FROM Comic c WHERE c.fechaCreacion = :fechaCreacion")})
public class Comic implements Serializable {

    @JoinColumn(name = "Usuario", referencedColumnName = "idUsuario")
    @ManyToOne
    private Usuario usuario;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idComic")
    private Integer idComic;
    @Size(max = 45)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idComic")
    private Collection<Entrega> entregaCollection;

    public Comic() {
    }

    public Comic(Integer idComic) {
        this.idComic = idComic;
    }

    public Comic(Integer idComic, Date fechaCreacion) {
        this.idComic = idComic;
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getIdComic() {
        return idComic;
    }

    public void setIdComic(Integer idComic) {
        this.idComic = idComic;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @XmlTransient
    public Collection<Entrega> getEntregaCollection() {
        return entregaCollection;
    }

    public void setEntregaCollection(Collection<Entrega> entregaCollection) {
        this.entregaCollection = entregaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idComic != null ? idComic.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comic)) {
            return false;
        }
        Comic other = (Comic) object;
        if ((this.idComic == null && other.idComic != null) || (this.idComic != null && !this.idComic.equals(other.idComic))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Comic[ idComic=" + idComic + " ]";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
}
