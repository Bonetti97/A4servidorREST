/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Comic;
import entity.Usuario;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author sergiorosadoferrer
 */
@Stateless
@Path("entity.comic")
public class ComicFacadeREST extends AbstractFacade<Comic> {

    @EJB
    private UsuarioFacadeREST usuarioFacadeREST;

    @PersistenceContext(unitName = "A4servidorREST")
    private EntityManager em;
    
   private Usuario usuarioSesion;

    public ComicFacadeREST() {
       
        super(Comic.class);
    }

    @POST
    @Path("{nombre}/{descripcion}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(@PathParam("nombre") String nombre, @PathParam("descripcion") String descripcion) {
        Comic c = new Comic();
        c.setDescripcion(descripcion);
        c.setNombre(nombre);
        c.setFechaCreacion(new Date());
   
        c.setUsuario(usuarioSesion);
        super.create(c);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Comic entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Comic find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
     @GET
    @Path("buscaFecha/{fecha}")
    @Produces({MediaType.APPLICATION_JSON})
     public List<Comic> buscarFecha (@PathParam("fecha") String d) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(d);
        Query q=this.em.createQuery("Select c from Comic c where c.fechaCreacion >= :fecha and c.usuario = :user");
        q.setParameter("user", usuarioSesion);
        q.setParameter("fecha", date);
        return q.getResultList();
    }
    
    @GET
    @Path("buscaNombre/{nombre}")
    @Produces({ MediaType.APPLICATION_JSON})
      public List<Comic> buscarNombre(@PathParam("nombre") String nombre){    
        Query q= this.em.createQuery("SELECT c from Comic c where c.nombre LIKE :nombre and c.usuario = :user");
        q.setParameter("nombre","%"+ nombre+"%");
        q.setParameter("user", usuarioSesion);
        
        List<Comic> lista = (List<Comic>)q.getResultList();
        if(lista.isEmpty()){
            return new ArrayList<>();
        }else{
            return lista;
        }
    }

    @GET
    @Path("findAll")
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<Comic> findAll() {
        return super.findAll();
    }
    
    @GET
    @Path("ordenaComicFecha")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Comic> ordenarFecha(){
        Query q = this.em.createQuery("SELECT c FROM Comic c where c.usuario = :user ORDER BY c.fechaCreacion DESC");
        q.setParameter("user", usuarioSesion);
        List<Comic> lista = (List<Comic>)q.getResultList();
        if(lista.isEmpty()){
            return new ArrayList<>();
        }else{
            return lista;
        } 
    }
    
    
    @GET
    @Path("ordenaComicAlfabetico")
    @Produces({ MediaType.APPLICATION_JSON})
      public List<Comic> ordenarAlfabetico(){
        Query q = this.em.createQuery("SELECT c FROM Comic c where and c.usuario = :user ORDER BY c.nombre ");
        q.setParameter("user", usuarioSesion);
        List<Comic> lista = (List<Comic>)q.getResultList();
        if(lista.isEmpty()){
            return new ArrayList<>();
        }else{
            return lista;
        }
    }
      
    @GET
    @Path("ordenaComicEntrega")
    @Produces({MediaType.APPLICATION_JSON})
      public List<Comic> ordenarPorEntregas(){
        
        Query q= this.em.createNativeQuery("SELECT c.* FROM Comic as c JOIN Entrega AS e on c.idComic=e.idComic GROUP BY e.idComic ORDER BY count(e.idEntrega) DESC");
        List<Comic> lista = (List<Comic>)q.getResultList();
        Iterator<Comic> it = lista.iterator();
        List<Comic> listaC = new ArrayList<>();
        
        //Añadimos comics del usuario.
        if(!lista.isEmpty()){
            while(it.hasNext()){ 
                Comic co = it.next();
                 if(co.getUsuario().equals(this.usuarioSesion)){
                        listaC.add(co); 
                 }        
            }
        } 
        
        //Añadimos comics con 0 entregas.
        lista = this.findByUsuario(usuarioSesion.getIdUsuario());
        it = lista.iterator();
        while(it.hasNext()){
            Comic co = it.next();
            if(co.getEntregaCollection().isEmpty()){
                listaC.add(co);
            }
        }
        
        
        if(listaC.isEmpty()){
            return new ArrayList<>();
        }else{
            return listaC;
        }    
    }
    
      

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Comic> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }
    
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    
    @Path("findByUsuario/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Comic> findByUsuario(@PathParam("id") Integer idUsuario) {
        Usuario u = usuarioFacadeREST.find(idUsuario);
        usuarioSesion = u;
        Query q = this.em.createQuery("Select c from Comic c where c.usuario = :id");
        q.setParameter("id", u);
        List<Comic> lista = (List<Comic>)q.getResultList();
        if(lista.isEmpty()){
            return new ArrayList<>();
        }else{
            return lista;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }    
}