/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Comic;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author sergiorosadoferrer
 */
@Stateless
@Path("entity.comic")
public class ComicFacadeREST extends AbstractFacade<Comic> {

    @PersistenceContext(unitName = "A4servidorREST")
    private EntityManager em;

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
     public List<Comic> buscarFecha (@PathParam("fecha") String d){
         String f=d.substring(0, 10);
       
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Query q=this.em.createQuery("Select c from Comic c where c.fechaCreacion >= :fecha");
        try {       
         Date date =format.parse(f);
        q.setParameter("fecha", format);
        } catch (ParseException ex) {
            Logger.getLogger(ComicFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }

        return q.getResultList();
    }
    
    @GET
    @Path("buscaNombre/{nombre}")
    @Produces({ MediaType.APPLICATION_JSON})
      public List<Comic> buscarNombre(@PathParam("nombre") String nombre){    
        Query q= this.em.createQuery("SELECT c from Comic c where c.nombre LIKE :nombre");
        q.setParameter("nombre","%"+ nombre+"%");
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
        Query q= this.em.createQuery("SELECT c FROM Comic c ORDER BY c.fechaCreacion DESC");
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
        Query q = this.em.createQuery("SELECT c FROM Comic c ORDER BY c.nombre ");
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
        List<Object[]> lista = (List<Object[]>)q.getResultList();
        Iterator<Object[]> it = lista.iterator();
        List<Comic> listaC = new ArrayList<>();
        if(!lista.isEmpty()){
            while(it.hasNext()){
            Comic co = this.find(it.next()[0]);
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

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}