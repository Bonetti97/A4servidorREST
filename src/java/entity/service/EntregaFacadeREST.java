/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import com.sun.xml.wss.impl.misc.Base64;
import entity.Comic;
import entity.Entrega;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
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
import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;


import org.json.JSONObject;

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
    @Path("{nombre}/{comic}")
    @Consumes({ MediaType.APPLICATION_JSON})
    public void create(@PathParam("nombre") String nombre, @JsonProperty("archivo") String archivo,@PathParam("comic") Integer comic) {
        Entrega e= new Entrega();
        Comic c = this.comicFacadeREST.find(comic);
        e.setIdComic(c);
        String url = archivo.substring(1,archivo.length()-1);
        e.setArchivo(url);
        e.setNombre(nombre);
        e.setFechaCreacion(new Date());
        super.create(e);
    }
   
    @PUT
    @Path("{id}/{nombre}")
    @Consumes("application/json")
    public void edit(@PathParam("id") Integer id, @PathParam("nombre") String nombre) {
        Entrega e= this.find(id);
        e.setNombre(nombre);
        super.edit(e);
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
    @Path("getComic/{id}")
    @Produces({ MediaType.TEXT_PLAIN})
    public Integer getIdComic(@PathParam("id") Integer id) {
        return super.find(id).getIdComic().getIdComic();
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
    @Consumes({ MediaType.APPLICATION_JSON})
      public List<Entrega> filtrarPorFecha(@PathParam("idComic") Integer idComic, @PathParam("fecha") String fecha) throws ParseException{
          SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
          Date date = format.parse(fecha);
          Comic c= this.comicFacadeREST.find(idComic);
          Query q= this.em.createQuery("SELECT e FROM Entrega e where (e.fechaCreacion >= :fecha AND e.idComic =:comic)");
          q.setParameter("comic", c);
          q.setParameter("fecha",date);
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
    
  

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}