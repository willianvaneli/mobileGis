package com.example.mobilegis.Models;



import android.database.Cursor;

import com.example.mobilegis.DAOs.OpenHelper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.util.LineStringExtracter;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polygon;
import org.spatialite.database.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.mobilegis.Config.Constantes.SRID;

public class Poligono extends Polygon implements Serializable {
    private boolean editavel;

    public Poligono (){
        this.editavel = false;
    }

    public Poligono (@org.jetbrains.annotations.NotNull org.locationtech.jts.geom.Polygon polygon, int id){

        this.editavel = false;
        this.setId(Integer.toString(id));

        Coordinate[] pontosExternos = polygon.getExteriorRing().getCoordinates();

        for (int i = 0 ; i < pontosExternos.length; i++){
            GeoPoint point = new GeoPoint(pontosExternos[i].y,pontosExternos[i].x);
            this.addPoint(point);
        }

        List<List<GeoPoint>> holes = new ArrayList<List<GeoPoint>>();
        for (int j = 0; j < polygon.getNumInteriorRing(); j++){
            Coordinate[] pontosInternos = polygon.getInteriorRingN(j).getCoordinates();
            List<GeoPoint> lst = new ArrayList<>();
            for (int k = 0; k < pontosInternos.length; k++){
                GeoPoint point = new GeoPoint(pontosInternos[k].y,pontosInternos[k].x);
                lst.add(point);
            }
            holes.add(lst);
        }
        this.setHoles(holes);


    }

    public Poligono (@org.jetbrains.annotations.NotNull org.locationtech.jts.geom.Polygon polygon, int id,int srid){

        if(polygon.getSRID()!= srid){
            try {
                OpenHelper sqliteHelper = OpenHelper.getInstance();
                SQLiteDatabase db = sqliteHelper.getWritableDatabase();
                String sql = String.format("Select AsText(ST_Transform(GeomFromText('%s' , %d ),%d)) AS res;", polygon.toText(),polygon.getSRID(),srid);

                Cursor cursor = db.rawQuery(sql,new String[]{});

                if (cursor != null && cursor.moveToFirst()) {
                    String pol = cursor.getString(cursor.getColumnIndex("res"));
                    if(pol != null && !pol.equals("")){
                        WKTReader reader = new WKTReader();
                        polygon = (org.locationtech.jts.geom.Polygon) reader.read(pol);
                        polygon.setSRID(srid);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        this.editavel = false;
        this.setId(Integer.toString(id));

        Coordinate[] pontosExternos = polygon.getExteriorRing().getCoordinates();

        for (int i = 0 ; i < pontosExternos.length; i++){
            GeoPoint point = new GeoPoint(pontosExternos[i].y,pontosExternos[i].x);
            this.addPoint(point);
        }

        List<List<GeoPoint>> holes = new ArrayList<List<GeoPoint>>();
        for (int j = 0; j < polygon.getNumInteriorRing(); j++){
            Coordinate[] pontosInternos = polygon.getInteriorRingN(j).getCoordinates();
            List<GeoPoint> lst = new ArrayList<>();
            for (int k = 0; k < pontosInternos.length; k++){
                GeoPoint point = new GeoPoint(pontosInternos[k].y,pontosInternos[k].x);
                lst.add(point);
            }
            holes.add(lst);
        }
        this.setHoles(holes);


    }

    public boolean getEditavel(){
        return editavel;
    }

    public void setEditavel(boolean editavel) {
        this.editavel = editavel;
    }


    // CONVERTE O POLYGON OVERLAY DO MAPA PARA POLYGON JTS
    public org.locationtech.jts.geom.Polygon getJTSPolygon(){
        GeometryFactory factory = new GeometryFactory();

        List<GeoPoint> ringOriginal = this.getPoints();
        Coordinate[] cordinate = new Coordinate[ringOriginal.size()];

        for (int i = 0 ; i < ringOriginal.size(); i++){
            cordinate[i] = new Coordinate(ringOriginal.get(i).getLongitude(),ringOriginal.get(i).getLatitude());
        }

        List<List<GeoPoint>> holes = this.getHoles();
        LinearRing[] ringHoles = new LinearRing[holes.size()];
        for (int j = 0 ; j < holes.size(); j++){
            Coordinate[] cordinateHole = new Coordinate[holes.get(j).size()];
            for (int k =0; k < holes.get(j).size(); k++){
                cordinateHole[k] = new Coordinate(holes.get(j).get(k).getLongitude(),holes.get(j).get(k).getLatitude());
            }
            ringHoles[j] = factory.createLinearRing(cordinateHole);
        }

        LinearRing lr = factory.createLinearRing(cordinate);


        org.locationtech.jts.geom.Polygon polygon = factory.createPolygon(lr,ringHoles);
        polygon.setSRID(4326);

        return  polygon;
    }


    public double maiorX(){
        double maiorX = -999999;
        for (int i = 0 ; i < this.getPoints().size() ; i ++ ){
            if ( maiorX < this.getPoints().get(i).getLongitude())
                maiorX = this.getPoints().get(i).getLongitude();
        }
        return maiorX;
    }

    public double maiorY(){
        double maiorY = -999999;
        for (int i = 0 ; i < this.getPoints().size() ; i ++ ){
            if ( maiorY < this.getPoints().get(i).getLatitude())
                maiorY = this.getPoints().get(i).getLatitude();
        }
        return maiorY;
    }

    public double menorX(){
        double menorX = 999999;
        for (int i = 0 ; i < this.getPoints().size() ; i ++ ){
            if ( menorX > this.getPoints().get(i).getLongitude())
                menorX = this.getPoints().get(i).getLongitude();
        }
        return menorX;
    }

    public double menorY(){
        double menorY = 999999;
        for (int i = 0 ; i < this.getPoints().size() ; i ++ ){
            if ( menorY > this.getPoints().get(i).getLatitude())
                menorY = this.getPoints().get(i).getLatitude();
        }
        return menorY;
    }



    public double areaQuadradoEnvelope(){
        double lado;
        if ( (maiorX() - menorX()) > (maiorY() - menorY()) ){
            lado = maiorX() - menorX();
        }else{
            lado = maiorY() - menorY();
        }
//        return lado*lado*6371000*1000;
        return Math.toRadians(lado*lado)* 6371000 * 100000;
    }

    public Poligono Envelope(){
        Poligono poligono = new Poligono();
        double menorY = menorY();
        double maiorY = maiorY();
        double menorX = menorX();
        double maiorX = maiorX();

        poligono.addPoint(new GeoPoint(menorY , menorX));
        poligono.addPoint(new GeoPoint(menorY , maiorX));
        poligono.addPoint(new GeoPoint(maiorY , maiorX));
        poligono.addPoint(new GeoPoint(maiorY , menorX));

        return poligono;
    }

    public GeoPoint centroidAprox(){
        double x = (maiorX() + menorX())/2;
        double y = (maiorY() + menorY())/2;
        return new GeoPoint(y,x);
    }

    public double getArea(){
        double area = 0;
        org.locationtech.jts.geom.Polygon pol = this.getJTSPolygon();
        OpenHelper sqliteHelper = OpenHelper.getInstance();
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();

        String sql = String.format("Select ST_Area(ST_Transform(GeomFromText('%s' , %d ),%d)) AS area;", pol.toText(),4326,SRID);

        Cursor cursor = db.rawQuery(sql,new String[]{});

        if (cursor != null && cursor.moveToFirst()) {
            area = cursor.getDouble(cursor.getColumnIndex("area"));

        }

        return area;
    }


    private  Geometry polygonize(Geometry geometry) {
        List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
        org.locationtech.jts.geom.Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return geometry.getFactory().createGeometryCollection(polyArray);
    }

    public Geometry splitPolygon( Geometry line) {
        org.locationtech.jts.geom.Polygon poly = this.getJTSPolygon();
        Geometry nodedLinework = poly.getBoundary().union(line);
        Geometry polys = polygonize(nodedLinework);

        // Only keep polygons which are inside the input
        List output = new ArrayList();
        for (int i = 0; i < polys.getNumGeometries(); i++) {
            org.locationtech.jts.geom.Polygon candpoly = (org.locationtech.jts.geom.Polygon) polys.getGeometryN(i);
            if (poly.contains(candpoly.getInteriorPoint())) {
                output.add(candpoly);
            }
        }
        return poly.getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(output));
    }



}
