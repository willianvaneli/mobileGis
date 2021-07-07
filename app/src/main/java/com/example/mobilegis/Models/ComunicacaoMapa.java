package com.example.mobilegis.Models;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.util.List;


public interface ComunicacaoMapa {
    public void salvaPoligono( Polygon poligono, int id);
    public void remembrar(List<Integer> ids);
    public void desmembrar(Geometry geoms, int id);
}
