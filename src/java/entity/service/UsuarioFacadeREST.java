/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity.service;

import entity.Comic;
import entity.Usuario;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.persistence.Query;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.JSONException;
//import org.json.JSONObject;


/**
 *
 * @author ubuntu
 */
@Stateless
@Path("entity.usuario")
public class UsuarioFacadeREST extends AbstractFacade<Usuario> {

    private int idUsuarioSesion;

    @PersistenceContext(unitName = "A4servidorREST")
    private EntityManager em;

    public UsuarioFacadeREST() {
        super(Usuario.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_JSON})
    public void create(Usuario entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Usuario entity) {
        super.edit(entity);
    }
    
    @PUT
    @Path("premium")
    @Consumes({MediaType.APPLICATION_JSON})
    public void premium() {
        Usuario us = this.find(this.idUsuarioSesion);
        us.setPermiso(2);
        System.out.println("Permiso usuario " + us.getPermiso());
        super.edit(us);
    }

    
    
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Usuario find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON})
    public List<Usuario> findAll() {
        return super.findAll();
    }
    
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    @GET
    @Path("getID")
    @Produces(MediaType.TEXT_PLAIN)
    public int getID() {
        return idUsuarioSesion;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @POST
    @Path("loginService")
    @Produces({MediaType.APPLICATION_FORM_URLENCODED})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String loginService(@FormParam("idtoken") String idtoken) throws IOException, JSONException{
        String link ="https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="+idtoken;
        URL url = new URL(link);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");  
        BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String result = readAll(rd);        
        JSONObject obj = new JSONObject(result);
        String token = (String) obj.get("sub"); 
        idUsuarioSesion = this.comprobarUsuario(token);
        return String.valueOf(idUsuarioSesion);
   }
    
    
     private int comprobarUsuario(String userId){
        
        Usuario u = this.findByToken(userId);
        
        if(u != null){    
            
            return u.getIdUsuario();
        }else{ 
            u = new Usuario();
            u.setIdtoken(userId);
            u.setPermiso(1);
            u.setComicCollection(new ArrayList<>());
            this.create(u);
            Usuario u2 = this.findByToken(userId);
            return u2.getIdUsuario();       
        }        
    }
    
    private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }
    
    
    private Usuario findByToken(String token){
        Query q = this.em.createQuery("SELECT u FROM Usuario u where u.idtoken = :token");
        q.setParameter("token",token);
        Usuario u;
        if(!q.getResultList().isEmpty()){
            u = (Usuario)q.getResultList().get(0);
            return u;
        }else{
            return null;
        }     
       
        
    }
        
    }
    
  

    
     
   


