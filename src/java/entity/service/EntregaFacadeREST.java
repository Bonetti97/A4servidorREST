/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import com.sun.xml.wss.impl.misc.Base64;
import entity.Comic;
import entity.Entrega;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebParam;
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
@Path("entity.entrega")
public class EntregaFacadeREST extends AbstractFacade<Entrega> {

    @EJB
    private ComicFacadeREST comicFacadeREST;

    @PersistenceContext(unitName = "A4servidorREST")
    private EntityManager em;
    
    

    public EntregaFacadeREST() {
        super(Entrega.class);
    }

    @POST
    @Override
    @Consumes({ MediaType.APPLICATION_JSON})
    public void create(Entrega entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Entrega entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Entrega find(@PathParam("id") Integer id) {
        return super.find(id);
    }
     
      @GET
    @Path("ordenaEntregaFecha/{idComic}")
    @Produces({ MediaType.APPLICATION_JSON})
      public List<Entrega> ordenarFecha2(@PathParam("idComic") Integer idComic){
        Comic c= this.comicFacadeREST.find(idComic);
        Query q= this.em.createQuery("SELECT e FROM Entrega e WHERE e.idComic =:comic ORDER BY e.fechaCreacion ASC");
        q.setParameter("comic", c);
        List<Entrega> lista = (List<Entrega>)q.getResultList();
        if(lista.isEmpty()){
            return new ArrayList<>();
        }else{
            return lista;
        } 
    }  
      
      
    @GET
    @Path("ordenaEntregaNombreInverso/{idComic}")
    @Produces({ MediaType.APPLICATION_JSON})
      public List <Entrega> ordenarNombreInverso(@PathParam("idComic") Integer idComic){
            Comic c= this.comicFacadeREST.find(idComic);
          Query q= this.em.createQuery("SELECT e FROM Entrega e WHERE e.idComic =:comic ORDER BY e.nombre DESC ");
         q.setParameter("comic", c);
          List<Entrega> lista = (List<Entrega>)q.getResultList();
          if(lista.isEmpty()){
            return new ArrayList<>();
          }else{
            return lista;
          }
      } 
      
    @GET
    @Path("filtraEntregaFecha/{idComic}/{fecha}")
    @Produces({ MediaType.APPLICATION_JSON})
      public List<Entrega> filtrarPorFecha(@PathParam("idComic") Integer idComic, @PathParam("fecha") Date fecha){
          Comic c= this.comicFacadeREST.find(idComic);
          Query q= this.em.createQuery("SELECT e FROM Entrega e where (e.fechaCreacion >= :fecha AND e.idComic =:comic)");
          q.setParameter("comic", c);
          q.setParameter("fecha", fecha);
          List<Entrega> lista = (List<Entrega>)q.getResultList();
          if(lista.isEmpty()){
            return new ArrayList<>();
          }else{
            return lista;
          } 
      } 
      
    @GET
    @Path("filtraEntregaComic/{idComic}")
    @Produces({MediaType.APPLICATION_JSON})
      public List<Entrega> entregasPorComic(@PathParam("idComic") Integer idComic){
          Comic c= this.comicFacadeREST.find(idComic);
          Query q = this.em.createQuery("SELECT e FROM Entrega e where e.idComic = :comic");
          q.setParameter("comic", c);
           List<Entrega> lista = (List<Entrega>)q.getResultList();
          if(lista.isEmpty()){
            return new ArrayList<>();
          }else{
            return lista;
          } 
      } 
      

    @GET
    @Path ("findAll")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Entrega> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Entrega> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    @GET
    @Path("getFoto/{idFoto}")
    @Produces({MediaType.APPLICATION_JSON})
    public String fotoBase64(@PathParam("idFoto") Integer idFoto){
     Entrega nuevaEntrega=super.find(idFoto);
         String fotoB64=Base64.encode(nuevaEntrega.getArchivo());
         return fotoB64;
      }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}