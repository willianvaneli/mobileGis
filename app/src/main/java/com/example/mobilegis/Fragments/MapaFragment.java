package com.example.mobilegis.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mobilegis.Models.ComunicacaoMapa;
import com.example.mobilegis.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.modules.ArchiveFileFactory;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static android.widget.RelativeLayout.ALIGN_PARENT_BOTTOM;

public class MapaFragment  extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    Context context;
    ComunicacaoMapa comunicator;
    MapView map = null;
    List<Camada> camadas;
    private ScaleBarOverlay mScaleBarOverlay;
    Camada camadaAtual;
    View view;
    boolean infoWindow = false;
    double areaPolEdit;
    private boolean zoomHabilitado;
    private OverlayZoom overlayZoom;
    private float fatorDensity;
    private boolean calcEnvelope = true;

    //Atributos relativos a edição
    private List<GeoPoint> geoPoints;
    boolean pintar = false;
    List<List<GeoPoint>> buffer = new ArrayList<>();
    Poligono poligonoAtual;
    FloatingActionButton fab;
    FloatingActionButton save;
    FloatingActionButton limpar;
    FloatingActionButton undo;
    MapEventsOverlay overlayEvents;
    List<Overlay> overlaysEdicao;
    private boolean mapCad = false;

    //Desmembrar
    private List<GeoPoint> linePoints;
    FloatingActionButton desmembrar;
    List<Overlay> overlaysDesmembrar;
    private boolean desmembramento;
    private MapEventsOverlay evDesmembrar;
    private Camada camadaDesmembrar;

    //Remembrar
    private FloatingActionButton remembrar;
    private List<Poligono> lstSelecionados;
    private boolean multiselecao;

    //GPS
    FloatingActionButton location;
    private Marker locationAtual;
    private boolean startGps;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient client;
    GoogleApiClient mGoogleApiClient;
    private LocationRequest request;
    private boolean changePos;
    protected Location location_origem;

    //Croqui
    private boolean croqui = false;
    private List<String> croquiOptions;


    // Desenho Fundo
    private Camada camadaFundo;


    public boolean isInfoWindow() {
        return infoWindow;
    }

    public void setInfoWindow(boolean infoWindow) {
        this.infoWindow = infoWindow;
    }

    Polygon.OnClickListener listener = new Polygon.OnClickListener() {
        @Override
        public boolean onClick(Polygon polygon, MapView mapView, GeoPoint eventPos) {
            InfoWindow.closeAllInfoWindowsOn(map);
            if (multiselecao) {
                if (polygon.getTitle() != null) {
                    if (lstSelecionados.contains((Poligono) polygon)) {
                        polygon.setStrokeColor(Color.rgb(0, 0, 255));
                        lstSelecionados.remove((Poligono) polygon);
                    } else {
                        lstSelecionados.add((Poligono) polygon);
                        poligonoAtual = (Poligono) polygon;
                        polygon.setStrokeColor(Color.rgb(255, 0, 0));
                    }
                }
            } else if (croqui) {
                limparSelecao();
                poligonoAtual = (Poligono) polygon;
                polygon.setStrokeColor(Color.rgb(255, 0, 0));
                mostraDialogoCroqui(polygon);

            } else {
                if (polygon.getTitle() != null) {
                    limparSelecao();
                    poligonoAtual = (Poligono) polygon;
                    polygon.setStrokeColor(Color.rgb(255, 0, 0));
//                InfoWindow.closeAllInfoWindowsOn(map);
                    poligonoAtual.showInfoWindow();
                }
            }
            return true;
        }
    };

    public MapView getMap() {
        return map;
    }

    public void setMap(MapView map) {
        this.map = map;
    }

    public double getAreaPolEdit() {
        return areaPolEdit;
    }

    public void setAreaPolEdit(double areaPolEdit) {
        this.areaPolEdit = areaPolEdit;
    }

    public org.locationtech.jts.geom.Polygon getPoligonoAtual() {
        if (poligonoAtual.getPoints().size() > 3) {
            return poligonoAtual.getJTSPolygon();
        } else {
            return null;
        }
    }

    private void mostraDialogoCroqui(final Polygon polygon) {
        final DialogMenssage dialogMenssage = new DialogMenssage(getActivity(), "Selecione o tipo de benfeitoria");

        dialogMenssage.setImage(R.drawable.baseline_assignment_black_48dp);

        dialogMenssage.setRadioOptions(croquiOptions);

        dialogMenssage.mostrarDialogoPersonalizado();

        dialogMenssage.getBtnCancelar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMenssage.dismiss();
            }
        });

        dialogMenssage.getBtnConfirmar().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = dialogMenssage.getSelecionado();
                if (id != -1) {
                    polygon.setTitle(croquiOptions.get(id));
                }
                dialogMenssage.dismiss();
            }
        });
    }

    public org.locationtech.jts.geom.Polygon getPoligonoEditavel() {
        salvaPoligono();
        if (poligonoAtual.getPoints().size() > 3) {
            return poligonoAtual.getJTSPolygon();
        } else {
            return null;
        }
    }

    private void setCenter(Poligono poligono) {
        map.getController().setCenter(this.poligonoAtual.centroidAprox());
        setZoom(poligono.areaQuadradoEnvelope());
    }

    public void setPoligonoAtual(org.locationtech.jts.geom.Polygon poligonoAtual, int id) {
        this.poligonoAtual = new Poligono(poligonoAtual, id);
        camadaAtual.addObjeto(this.poligonoAtual);
        map.getController().setCenter(this.poligonoAtual.centroidAprox());
        setZoom(this.poligonoAtual.areaQuadradoEnvelope());
        atualizaInicial();
        setZoom(false);
    }

    // Recebe um poligono e coloca-o como em edição
    public void setPoligonoEditavel(org.locationtech.jts.geom.Polygon poligonoAtual, int id) {
        alteraEstadoPintura();
        this.poligonoAtual = new Poligono(poligonoAtual, id);
        this.geoPoints = this.poligonoAtual.getPoints();
        // Retira último geopoint devido repetição natural do poligono
        this.geoPoints.remove(this.geoPoints.size() - 1);
        map.getController().setCenter(this.poligonoAtual.centroidAprox());
        setZoom(this.poligonoAtual.areaQuadradoEnvelope());
        atualizaMapa();
        incluiAlteracao();
        setZoom(false);
    }

    // Define o zoom do mapa de acordo com a área do "envelope" dos poligonos
    private void setZoom(Double area) {
        if (area > 25999999.999)
            map.getController().setZoom(12.0);
        else if (25000000 > area && area > 20999999.999)
            map.getController().setZoom(13.5);
        else if (20000000 > area && area > 15999999.999)
            map.getController().setZoom(15.0);
        else if (16000000 > area && area > 499999.999)
            map.getController().setZoom(15.5);
        else if (500000 > area && area > 279999.999)
            map.getController().setZoom(16.0);
        else if (280000 > area && area > 174999.999)
            map.getController().setZoom(16.5);
        else if (175000 > area && area > 62499.999)
            map.getController().setZoom(17.0);
        else if (62500 > area && area > 39061.999)
            map.getController().setZoom(17.5);
        else if (39062 > area && area > 15624.999)
            map.getController().setZoom(18.0);
        else if (15625 > area && area > 9764.999)
            map.getController().setZoom(19.0);
        else if (9765 > area && area > 3905.999)
            map.getController().setZoom(19.5);
        else if (3906 > area && area > 979.999)
            map.getController().setZoom(20.0);
        else if (980 > area && area > 599.999)
            map.getController().setZoom(20.5);
        else if (600 > area && area > 399.999)
            map.getController().setZoom(21.0);
        else if (400 > area && area > 199.999)
            map.getController().setZoom(21.5);
        else if (200 > area)
            map.getController().setZoom(22.0);
    }

    public ComunicacaoMapa getComunicator() {
        return comunicator;
    }

    public void setComunicator(ComunicacaoMapa comunicator) {
        this.comunicator = comunicator;
    }

    public List<Camada> getCamadas() {
        return camadas;
    }

    public void setCamadas(List<Camada> camadas) {
        this.camadas = camadas;
    }

    public void addCamada(Camada camada) {
        this.camadas.add(camada);
        if (calcEnvelope) {
            map.getController().setCenter(calculaEnvelopeCamada(camada).centroidAprox());
            setZoom(calculaEnvelopeCamada(camada).areaQuadradoEnvelope());
        }
        atualizaInicial();
    }

    private Poligono calculaEnvelopeCamada(Camada camada) {
        Poligono poligono = new Poligono();
        double maiorX = -999999;
        double menorX = 999999;
        double maiorY = -999999;
        double menorY = 999999;

        for (int i = 0; i < camada.getObjetos().size(); i++) {
            if (maiorX < ((Poligono) camada.getObjetos().get(i)).maiorX())
                maiorX = ((Poligono) camada.getObjetos().get(i)).maiorX();
            if (menorX > ((Poligono) camada.getObjetos().get(i)).menorX())
                menorX = ((Poligono) camada.getObjetos().get(i)).menorX();
            if (maiorY < ((Poligono) camada.getObjetos().get(i)).maiorY())
                maiorY = ((Poligono) camada.getObjetos().get(i)).maiorY();
            if (menorY > ((Poligono) camada.getObjetos().get(i)).menorY())
                menorY = ((Poligono) camada.getObjetos().get(i)).menorY();
        }
        poligono.addPoint(new GeoPoint(menorY, menorX));
        poligono.addPoint(new GeoPoint(menorY, maiorX));
        poligono.addPoint(new GeoPoint(maiorY, maiorX));
        poligono.addPoint(new GeoPoint(maiorY, menorX));

        return poligono;
    }

    public List<GeoPoint> getGeoPoints() {
        return geoPoints;
    }

    public void setGeoPoints(List<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mapa, container, true);

        this.fatorDensity = getResources().getDisplayMetrics().density;
        super.onCreate(savedInstanceState);

        context = inflater.getContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));


        if (camadas == null) {
            camadas = new ArrayList<>();
        }
        if (geoPoints == null) {
            geoPoints = new ArrayList<>();
        }
        linePoints = new ArrayList<>();
        lstSelecionados = new ArrayList<Poligono>();


        overlaysEdicao = new ArrayList<Overlay>();
        overlaysDesmembrar = new ArrayList<Overlay>();

        map = (MapView) view.findViewById(R.id.map);

        map.setUseDataConnection(false);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(22.0);
        map.setMinZoomLevel(12.0);
        IMapController mapController = map.getController();
        mapController.setZoom(12.5);
        GeoPoint startPoint = new GeoPoint(-20.2215, -40.3078);
        mapController.setCenter(startPoint);

        //Escala na tela.
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(this.mScaleBarOverlay);

        // Declaração dos floating buttons
        iniciaFloatingButtons();

        MapEventsReceiver mDes = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                GeoPoint gp = new GeoPoint(p.getLatitude(), p.getLongitude());
                if (linePoints.size() == 0) {
                    linePoints.add(gp);
                    addMarker(gp);
                } else if (linePoints.size() > 0) {
                    linePoints.add(gp);
                    desenhaLinha();
                    atualizaMapa();
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return true;
            }
        };
        evDesmembrar = new MapEventsOverlay(mDes);


        // Recebe os eventos do mapa, escuta.
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {

                GeoPoint gp = new GeoPoint(p.getLatitude(), p.getLongitude());
                if (geoPoints.size() == 0) {
                    incluiAlteracao();
                    geoPoints.add(gp);
                    addMarker(gp);
                } else if (geoPoints.size() > 0) {
                    incluiAlteracao();
                    geoPoints.add(gp);
                    atualizaMapa();
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return true;
            }


        };

        overlayEvents = new MapEventsOverlay(mReceive);


        overlayZoom = new OverlayZoom(context);

        map.getOverlayManager().add(overlayZoom);

        camadaAtual = new Camada();
        camadas.add(camadaAtual);
        adicionarTiles2();
        inicializaParamPos();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    public void adicionarTiles() {
        //not even needed since we are using the offline tile provider only
        this.map.setUseDataConnection(false);

        //https://github.com/osmdroid/osmdroid/issues/330
        //custom image placeholder for files that aren't available
        map.getTileProvider().setTileLoadFailureImage(getResources().getDrawable(R.drawable.moreinfo_arrow));


        //first we'll look at the default location for tiles that we support
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/");
        if (f.exists()) {
            List<File> files = new ArrayList<>();
            File[] list = f.listFiles();
            Arrays.sort(list);


            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        continue;
                    }

                    String name = list[i].getName().toLowerCase();
                    if (!name.contains(".")) {
                        continue; //skip files without an extension
                    }
                    name = name.substring(name.lastIndexOf(".") + 1);
                    if (name.length() == 0) {
                        continue;
                    }
                    if (ArchiveFileFactory.isFileExtensionRegistered(name)) {
                        try {
                            String pname = list[i].getName().toLowerCase();
                            pname = pname.substring(0, pname.lastIndexOf("."));
                            if (pname.equals("sqlite")) {
                                files.add(list[i]);
                            } else {
                                files.add(list[i]);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                File[] lst = files.toArray(new File[files.size()]);

                try {
                    OfflineTileProvider tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver(getActivity()), lst);

                    map.setTileProvider(tileProvider);

                    String source = "";
                    IArchiveFile[] archives = tileProvider.getArchives();
                    if (archives.length > 0) {
                        //cheating a bit here, get the first archive file and ask for the tile sources names it contains
                        Set<String> tileSources = archives[0].getTileSources();
                        //presumably, this would be a great place to tell your users which tiles sources are available
                        if (!tileSources.isEmpty()) {
                            //ok good, we found at least one tile source, create a basic file based tile source using that name
                            //and set it. If we don't set it, osmdroid will attempt to use the default source, which is "MAPNIK",
                            //which probably won't match your offline tile source, unless it's MAPNIK
                            source = tileSources.iterator().next();
                            this.map.setTileSource(FileBasedTileSource.getSource(source));
                        } else {
                            this.map.setTileSource(TileSourceFactory.MAPNIK);
                        }

                    } else {
                        this.map.setTileSource(TileSourceFactory.MAPNIK);
                    }

                    this.map.invalidate();
                } catch (Exception e) {
                    e = e;
                }

            }

        } else {

        }
    }


    public void adicionarTiles2() {
        this.map.setUseDataConnection(false);
        map.getTileProvider().setTileLoadFailureImage(getResources().getDrawable(R.drawable.moreinfo_arrow));

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmdroid/");
        if (f.exists()) {
            List<File> files = new ArrayList<>();
            File[] list = f.listFiles();
            Arrays.sort(list);

            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isDirectory()) {
                        continue;
                    }

                    String name = list[i].getName().toLowerCase();
                    if (!name.contains(".")) {
                        continue; //skip files without an extension
                    }
                    name = name.substring(name.lastIndexOf(".") + 1);
                    if (name.length() == 0) {
                        continue;
                    }
                    if (ArchiveFileFactory.isFileExtensionRegistered(name)) {
                        try {
                            String pname = list[i].getName().toLowerCase();
                            pname = pname.substring(0, pname.lastIndexOf("."));
                            if (pname.equals("sqlite")) {
                                files.add(list[i]);
                            } else {
                                files.add(list[i]);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                File[] lst = files.toArray(new File[files.size()]);

                try {

                    OfflineTileProvider tileProvider = new OfflineTileProvider(new SimpleRegisterReceiver(context), lst);


                    map.setTileProvider(tileProvider);

                    String source = "";
                    IArchiveFile[] archives = tileProvider.getArchives();
                    if (archives.length > 0) {
                        //cheating a bit here, get the first archive file and ask for the tile sources names it contains
                        Set<String> tileSources = archives[0].getTileSources();
                        //presumably, this would be a great place to tell your users which tiles sources are available
                        if (!tileSources.isEmpty()) {
                            //ok good, we found at least one tile source, create a basic file based tile source using that name
                            //and set it. If we don't set it, osmdroid will attempt to use the default source, which is "MAPNIK",
                            //which probably won't match your offline tile source, unless it's MAPNIK
                            source = tileSources.iterator().next();
                            map.setTileSource(new XYTileSource(source, 12, 22, 256, ".png", new String[]{""}));

                        } else {
                            this.map.setTileSource(TileSourceFactory.MAPNIK);
                        }

                    } else {
                        this.map.setTileSource(TileSourceFactory.MAPNIK);
                    }

                    this.map.invalidate();
                } catch (Exception e) {
                    e = e;
                }

            }

        } else {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDetach();
        map.getTileProvider().clearTileCache();
        stopLocationUpdates();
    }


    //Função que altera o estado de edição de poligono, bloqueando ou liberando os escutadores e escondendo ou mostrando os botões de edição.
    public void alteraEstadoPintura() {
        if (pintar == true) {
            pintar = false;
            fab.setColorFilter(Color.parseColor("#FFFFFF"));
            setZoom(true);
            save.hide();
            limpar.hide();
            undo.hide();
            geoPoints.clear();
            overlaysEdicao.clear();
            atualizarSemOverlay();

        } else {
            pintar = true;
            fab.setColorFilter(Color.parseColor("#FFFFB300"));
            setZoom(false);
            save.show();
            limpar.show();
            undo.show();
            map.getOverlays().add(overlayEvents);
            InfoWindow.closeAllInfoWindowsOn(map);
            limparSelecao();
        }
    }

    //Função seta o estado do Zoom pelo multitouch e pelo controle de Zoom
    public void setZoom(boolean b) {
        if (b) {
            map.setMultiTouchControls(true);
            zoomHabilitado = true;
        } else {
            map.setMultiTouchControls(false);
            zoomHabilitado = false;
        }
    }

    //Função que faz voltar o estado do poligono
    public void undo() {
        if (buffer.size() > 1) {
            geoPoints.clear();
            copiaListaGeopoints(geoPoints, buffer.get(buffer.size() - 1));
            buffer.remove(buffer.size() - 1);
            atualizaMapa();
        }
    }

    //Função que salva estado do poligono em edição
    public void incluiAlteracao() {
        try {
            List<GeoPoint> lstTemp = new ArrayList<GeoPoint>(geoPoints.size());
            copiaListaGeopoints(lstTemp, geoPoints);
            buffer.add(lstTemp);
        } catch (Exception e) {
            Toast.makeText(map.getContext(), "erro " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Função que copia a lista de geopoints, criada pois o método clone() não funcionou corretamente.
    public void copiaListaGeopoints(List<GeoPoint> a, List<GeoPoint> b) {
        for (int i = 0; i < b.size(); i++) {
            a.add(new GeoPoint(b.get(i).getLatitude(), b.get(i).getLongitude()));
        }
    }

    //Função que salva o poligono em edição
    public void salvaPoligono() {
        if (geoPoints.size() > 0) {
            Poligono polygon = new Poligono();
            polygon.setPoints(geoPoints);
            polygon.addPoint(geoPoints.get(0));
            polygon.setStrokeWidth(3);
            polygon.setStrokeColor(Color.rgb(0, 0, 255));

            polygon.setOnClickListener(listener);
            polygon.setId("0");

            try {

            } catch (Exception e) {
                Toast.makeText(map.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            this.poligonoAtual = polygon;
            if (!mapCad) {
                geoPoints.clear();
                atualizaMapa();
            }
        }
    }

    public void limparSelecao() {
        InfoWindow.closeAllInfoWindowsOn(map);
        if (poligonoAtual != null) {
            poligonoAtual.setStrokeColor(Color.rgb(0, 0, 255));
        }
    }

    //Função que percorre todas as camadas e busca todos poligonos para exibilos no mapa, caso haja poligono em edição o mesmo também é colocado na tela
    public void atualizaMapa() {

        map.getOverlayManager().removeAll(overlaysEdicao);
        overlaysEdicao.clear();
        //Remove as info windowns abertas
        InfoWindow.closeAllInfoWindowsOn(map);
        //Remove os desenhos referentes ao poligono em edição
        map.invalidate();
        if (geoPoints.size() > 0) {
            inserirPoligono();
        }
        map.getOverlayManager().addAll(overlaysEdicao);
    }

    public void atualizaMapaLinha() {
        map.getOverlayManager().removeAll(overlaysDesmembrar);
        overlaysDesmembrar.clear();
        //Remove as info windowns abertas
        InfoWindow.closeAllInfoWindowsOn(map);
        //Remove os desenhos referentes ao poligono em edição
        map.invalidate();
        if (linePoints.size() > 0) {
            desenhaLinha();
        }
        map.getOverlayManager().addAll(overlaysDesmembrar);
    }

    public void atualizarSemOverlay() {
        map.getOverlayManager().clear();
        if (camadaFundo != null) {
            for (int j = 0; j < camadaFundo.getObjetos().size(); j++) {
                Poligono polygon = ((Poligono) camadaFundo.getObjetos().get(j));    //see note below
                polygon.setStrokeColor(camadaFundo.getColor());
                polygon.setStrokeWidth(3);
                //Escutador para selecionar poligono requisitado.
                map.getOverlayManager().add(polygon);

            }
        }
        for (int i = 0; i < camadas.size(); i++) {
            if (camadas.get(i).isVisivel()) {
                for (int j = 0; j < camadas.get(i).getObjetos().size(); j++) {
                    map.getOverlayManager().add((Poligono) camadas.get(i).getObjetos().get(j));
                }
            }
        }
        map.invalidate();
    }

    public void atualizaInicial() {
        if (camadaFundo != null) {
            for (int j = 0; j < camadaFundo.getObjetos().size(); j++) {
                Poligono polygon = ((Poligono) camadaFundo.getObjetos().get(j));    //see note below
                polygon.setStrokeColor(camadaFundo.getColor());
                polygon.setStrokeWidth(3);
                //Escutador para selecionar poligono requisitado.
                map.getOverlayManager().add(polygon);

            }
        }
        for (int i = 0; i < camadas.size(); i++) {
            if (camadas.get(i).isVisivel()) {
                for (int j = 0; j < camadas.get(i).getObjetos().size(); j++) {
                    Poligono polygon = ((Poligono) camadas.get(i).getObjetos().get(j));    //see note below
                    polygon.setStrokeColor(camadas.get(i).getColor());
                    polygon.setStrokeWidth(3);
                    if (infoWindow) {
                        polygon.setOnClickListener(listener);
//                    polygon.setTitle("Deseja ir para o cadastro do terreno?   ");
                        polygon.setInfoWindow(new PoligonoInfoWindow(map));
                    }
                    polygon.setInfoWindowLocation(polygon.centroidAprox());
                    //Escutador para selecionar poligono requisitado.
                    map.getOverlayManager().add(polygon);
                }
            }
        }
    }


    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        stopLocationUpdates();
    }


    //Recebe uma lista de Geopoints e desenha um poligono, invocando o método para criar marcadores em cada vértice
    public void inserirPoligono() {

        Poligono polygon = new Poligono();

        polygon.setPoints(geoPoints);
        polygon.addPoint(geoPoints.get(0));
        polygon.setStrokeWidth(3);
        polygon.setStrokeColor(Color.rgb(0, 0, 255));
        if (infoWindow) {
            polygon.setOnClickListener(listener);
            polygon.setInfoWindow(new PoligonoInfoWindow(map));
            polygon.setInfoWindowLocation(polygon.centroidAprox());
        }
        overlaysEdicao.add(polygon);
        //Criando marcadores para cada vértice
        for (GeoPoint i : geoPoints) {
            addMarker(i);
        }

        inserirVerticesVirtuais(polygon);

    }


    //Função que recebe um poligono e cria pontos virtuais entre os vértices. Estes pontos virtuais possuem escutadores que ao serem acionados
    // se transformam em vértices reais.

    //Função também põe as distâncias entre pontos e aplica a área no poligono
    public void inserirVerticesVirtuais(Poligono poligono) {
        if (poligono.getPoints().size() < 4) {
            GeoPoint geoPoint = new GeoPoint(
                    (poligono.getPoints().get(0).getLatitude() + poligono.getPoints().get(1).getLatitude()) / 2,
                    (poligono.getPoints().get(0).getLongitude() + poligono.getPoints().get(1).getLongitude()) / 2);
            Marker marker = new Marker(map);
            marker.setPosition(geoPoint);
            marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_preto12));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            // Ao ser clickado o vertice Virtual se transforma em vertice real
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    //Metodo que adiciona o vertice na posição correta
                    geoPoints.add(getIndexVirtual(marker.getPosition()), marker.getPosition());
                    atualizaMapa();
                    return false;
                }
            });

            //metodo que controla o vertice virtual enquanto em drag (movimento)
            marker.setDraggable(true);
            marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                int index = 0;

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    incluiAlteracao();
                    geoPoints.add(index, marker.getPosition());
                    atualizaMapa();
                    //desenhaPoligonoComLinhas();
                    if (!pintar) {
                        alteraEstadoPintura();
                    }


                }

                @Override
                public void onMarkerDragStart(Marker marker) {
//                    marker.setIcon(getResources().getDrawable(R.drawable.ponto_azul24));
//                    marker.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ponto_azul24,null));
                    marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_azul12));
                }
            });

//            map.getOverlayManager().add(marker);
            overlaysEdicao.add(marker);
        } else {
            for (int i = 0; i < poligono.getPoints().size() - 1; i++) {

                double distancia = getDistance(poligono.getPoints().get(i), poligono.getPoints().get(i + 1));

                if (mostrarMarkerVitrual(distancia)) {


                    final GeoPoint geoPoint = new GeoPoint(
                            (poligono.getPoints().get(i).getLatitude() + poligono.getPoints().get(i + 1).getLatitude()) / 2,
                            (poligono.getPoints().get(i).getLongitude() + poligono.getPoints().get(i + 1).getLongitude()) / 2);
                    Marker marker = new Marker(map);
                    marker.setPosition(geoPoint);
//                    marker.setIcon(this.getResources().getDrawable(R.drawable.ponto_preto24));
//                    marker.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.ponto_preto24,map.getContext().getTheme()));
                    marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_preto12));
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);


                    double zoom = Math.floor(map.getZoomLevelDouble());
                    if (zoom > 17) {
                        //Pega posição para verificar quais pontos são necessários para o cálculo de distância
                        //                    int pos = getIndexVirtual(marker.getPosition());
                        //                    double distancia = getDistance(poligono.getPoints().get(pos - 1),poligono.getPoints().get(pos));
                        //                    GeoPoint p = marker.getPosition();

                        Marker m = new Marker(map);
                        double ajuste = geoPoint.getLatitude() - (getDistText(zoom)) * fatorDensity;
                        if (geoPoint.getLatitude() < 0) {
                            m.setPosition(new GeoPoint(ajuste, geoPoint.getLongitude()));
                        } else {
                            m.setPosition(new GeoPoint(ajuste, geoPoint.getLongitude()));
                        }
                        if (distancia > 9) {
                            m.setTextLabelFontSize((int) (12 * fatorDensity));
                        } else {
                            m.setTextLabelFontSize((int) (8 * fatorDensity));
                        }
                        m.setTextLabelBackgroundColor(Color.parseColor("#B9FFFFFF"));
                        m.setTextIcon(String.format("%.2f m", distancia));
                        m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                return false;
                            }
                        });

                        //                    map.getOverlayManager().add(m);
                        overlaysEdicao.add(m);


                    }


                    marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
//                            incluiAlteracao();
                            geoPoints.add(getIndexVirtual(marker.getPosition()), marker.getPosition());
                            //geoPoints.remove(geoPoints.size()-1);
                            atualizaMapa();
                            return true;
                        }
                    });

                    marker.setDraggable(true);
                    marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                        int index = 0;

                        @Override
                        public void onMarkerDrag(Marker marker) {

                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            incluiAlteracao();
                            geoPoints.add(index, marker.getPosition());
                            atualizaMapa();
                            //desenhaPoligonoComLinhas();
                            if (!pintar) {
                                alteraEstadoPintura();
                            }

                        }

                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            index = getIndexVirtual(marker.getPosition());
                        }
                    });

                    //                map.getOverlayManager().add(marker);
                    overlaysEdicao.add(marker);
                }
            }

            areaPolEdit = poligono.getArea();
            Marker markerArea = new Marker(map);
            markerArea.setPosition(poligono.centroidAprox());
            markerArea.setTextLabelFontSize((int) (14 * fatorDensity));
            markerArea.setTextLabelBackgroundColor(Color.parseColor("#B9fbec5d"));
            markerArea.setTextIcon(String.format("%.2f m²", areaPolEdit));
//            map.getOverlayManager().add(markerArea);
            overlaysEdicao.add(markerArea);
        }
    }

    private double getDistText(double zoom) {
        if (zoom == 22) {
            return 0.000005;
        } else if (zoom == 21) {
            return 0.000008;
        } else if (zoom == 20) {
            return 0.000012;
        } else if (zoom == 19) {
            return 0.00003;
        } else if (zoom == 18) {
            return 0.00005;
        }
        return -1;
    }

    private boolean mostrarMarkerVitrual(double distancia) {
        double zoom = map.getZoomLevelDouble();
        if (zoom > 21.9) {
            if (distancia > 1) {
                return true;
            }
        } else if (zoom > 20.9) {
            if (distancia > 2) {
                return true;
            }
        } else if (zoom > 19.9) {
            if (distancia > 4) {
                return true;
            }
        } else if (zoom > 18.9) {
            if (distancia > 10) {
                return true;
            }
        } else if (zoom > 17.9) {
            if (distancia > 17) {
                return true;
            }
        }
        return false;
    }

    //Função que recebe um geopoint de um vértice virtual e descobre qual o indice que o mesmo deve ser adicionado a lista de geopoints
    public int getIndexVirtual(GeoPoint point) {
        int index = 0;
        int i;
        for (i = 0; i < geoPoints.size() - 1; i++) {
            if (point.getLatitude() == (geoPoints.get(i).getLatitude() + geoPoints.get(i + 1).getLatitude()) / 2 &&
                    point.getLongitude() == (geoPoints.get(i).getLongitude() + geoPoints.get(i + 1).getLongitude()) / 2) {
                return i + 1;
            }
        }
        if (point.getLatitude() == (geoPoints.get(i).getLatitude() + geoPoints.get(0).getLatitude()) / 2 &&
                point.getLongitude() == (geoPoints.get(i).getLongitude() + geoPoints.get(0).getLongitude()) / 2) {
            return i + 1;
        }

        return index;
    }

    //TESTADO E FUNCIONANDO
    //Desenha linha editavel
    public void desenhaLinha() {
        Polyline line = new Polyline();
        line.setPoints(linePoints);
        overlaysDesmembrar.add(line);
        map.getOverlayManager().addAll(overlaysDesmembrar);
        for (GeoPoint i : linePoints) {
            addMarkerLinha(i);
        }
        map.invalidate();

    }

    //Recebe um Geopoint e retorna o index do mesmo na lista de Geopoints global.
    public int getIndexGeoPoint(GeoPoint point) {

        for (int i = 0; i < geoPoints.size(); i++) {
            if (geoPoints.get(i).getLatitude() == point.getLatitude() && geoPoints.get(i).getLongitude() == point.getLongitude()) {
                return i;
            }
        }
        return -1;
    }

    //Recebe um Geopoint e retorna o index do mesmo na lista de linepoints global.
    public int getIndexLinePoints(GeoPoint point) {

        for (int i = 0; i < linePoints.size(); i++) {
            if (linePoints.get(i).getLatitude() == point.getLatitude() && linePoints.get(i).getLongitude() == point.getLongitude()) {
                return i;
            }
        }
        return -1;
    }

    public void limpaInfoWindows() {
        for (int i = 0; i < map.getOverlayManager().overlays().size(); i++) {
            if (map.getOverlayManager().overlays().get(i) instanceof Marker) {
                ((Marker) map.getOverlayManager().overlays().get(i)).closeInfoWindow();
            }
        }
    }

    // ADICIONA UM MARCADOR NO MAPA
    public void addMarker(GeoPoint center) {
        Marker marker = new Marker(map);
        marker.setPosition(center);
        marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_vermelho12));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        marker.setDraggable(true);


        marker.setTitle("Remover vértice?   ");
        marker.setInfoWindow(new CustomInfoWindow(map));


        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                limpaInfoWindows();
                marker.showInfoWindow();
                return true;
            }
        });

        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            int index = 0;

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_vermelho12));
                geoPoints.add(index, marker.getPosition());
                atualizaMapa();
                if (!pintar) {
                    alteraEstadoPintura();
                }

            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_azul12));
                incluiAlteracao();
                index = getIndexGeoPoint(marker.getPosition());
                geoPoints.remove(index);
            }
        });
        overlaysEdicao.add(marker);
        map.getOverlayManager().addAll(overlaysEdicao);
    }

    public void addMarkerLinha(GeoPoint center) {
        Marker marker = new Marker(map);
        marker.setPosition(center);
        marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_vermelho12));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setDraggable(true);
        marker.setTitle("Remover vértice?   ");
        marker.setInfoWindow(new CustomInfoWindow(map));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                limpaInfoWindows();
                marker.showInfoWindow();
                return true;
            }
        });

        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            int index = 0;

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_vermelho12));
                linePoints.add(marker.getPosition());
                atualizaMapaLinha();
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.setIcon(ContextCompat.getDrawable(context, R.drawable.ponto_azul12));
                index = getIndexLinePoints(marker.getPosition());
                linePoints.remove(index);
            }
        });
        overlaysDesmembrar.add(marker);
        map.getOverlayManager().addAll(overlaysDesmembrar);
    }

    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // LISTENER
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (l != null) {
            Log.i("LOG", "latitude: " + l.getLatitude());
            Log.i("LOG", "longitude: " + l.getLongitude());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed(" + connectionResult + ")");
    }


    // CLASSE PARA USAR INFO WINDOW NO MARKER, POSSIBILITA O USO DO BOTÃO UTILIZADO AQUI PARA DELETAR O MARKER
    public class CustomInfoWindow extends MarkerInfoWindow {
        public CustomInfoWindow(MapView mapView) {
            super(R.layout.bonuspack_bubble_edited, mapView);
        }

        @Override
        public void onOpen(final Object item) {
            final Marker marker = (Marker) item;

            TextView title = (TextView) mView.findViewById(R.id.bubble_title);
            title.setVisibility(View.VISIBLE);
            title.setText(marker.getTitle());

            TextView snipet = (TextView) mView.findViewById(R.id.bubble_description);
            snipet.setVisibility(View.GONE);

            Button btnYes = (Button) (mView.findViewById(R.id.bubble_yes));
            btnYes.setBackground(ContextCompat.getDrawable(context, R.drawable.check_circle_48dp));
            btnYes.setVisibility(View.VISIBLE);
            btnYes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    incluiAlteracao();
                    int index = getIndexGeoPoint(marker.getPosition());
                    geoPoints.remove(index);
                    atualizaMapa();
                    marker.closeInfoWindow();
//                    Toast.makeText(view.getContext(), "Button SIM clicked", Toast.LENGTH_LONG).show();
                }
            });

            Button btnNo = (Button) (mView.findViewById(R.id.bubble_no));
            btnNo.setVisibility(View.VISIBLE);
            btnNo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    marker.closeInfoWindow();
//                    Toast.makeText(view.getContext(), "Button NO clicked", Toast.LENGTH_LONG).show();
                }
            });

            Button btnLocation = (Button) (mView.findViewById(R.id.location));
            btnLocation.setVisibility(View.GONE);


        }
    }

    private class PoligonoInfoWindow extends InfoWindow {
        public PoligonoInfoWindow(MapView mapView) {
            super(R.layout.bonuspack_bubble_edited, mapView);
        }

        @Override
        public void onOpen(Object item) {
            final Poligono poligono = (Poligono) item;

            TextView title = (TextView) mView.findViewById(R.id.bubble_title);
            title.setText(poligono.getTitle());

            TextView snipet = (TextView) mView.findViewById(R.id.bubble_description);
            snipet.setVisibility(View.GONE);

            Button btnYes = (Button) (mView.findViewById(R.id.bubble_yes));
            btnYes.setVisibility(View.VISIBLE);
            btnYes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (poligono.getTitle() != null) {
                        comunicator.salvaPoligono(poligono.getJTSPolygon(), Integer.parseInt(poligono.getId()));
                    }
//                    Toast.makeText(view.getContext(), "Button SIM clicked do lote " + poligono.getId(), Toast.LENGTH_LONG).show();
                }
            });

            Button btnNo = (Button) (mView.findViewById(R.id.bubble_no));
            btnNo.setVisibility(View.VISIBLE);
            btnNo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    InfoWindow.closeAllInfoWindowsOn(map);
//                    Toast.makeText(view.getContext(), "Button NO clicked", Toast.LENGTH_LONG).show();
                }
            });

            Button btnLocation = (Button) (mView.findViewById(R.id.location));
            btnLocation.setVisibility(View.VISIBLE);
            btnLocation.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    final GeoPoint point = poligono.centroidAprox();
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    }

                    client.getLastLocation()
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {

                                        //obtém a última localização conhecida
                                        location_origem = task.getResult();
                                        String link = "https://www.google.com/maps/dir/?api=1&origin=" + Objects.toString(location_origem.getLatitude()) + "%2C" + Objects.toString(location_origem.getLongitude())
                                                + "&destination=" + Objects.toString(point.getLatitude()) + "%2C" + Objects.toString(point.getLongitude());

                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(context,"falha ao capturar localização origem",Toast.LENGTH_LONG);
                                        //Não há localização conhecida ou houve uma excepção
                                        //A excepção pode ser obtida com task.getException()
                                    }
                                }
                            });


                }
            });

        }

        @Override
        public void onClose() {

        }
    }

    public static double getDistance(GeoPoint StartP, GeoPoint EndP) {
        double lat1 = StartP.getLatitude();
        double lat2 = EndP.getLatitude();
        double lon1 = StartP.getLongitude();
        double lon2 = EndP.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }



    public void limpaInfoWindow() {
        InfoWindow.closeAllInfoWindowsOn(map);
    }

    public void mapCad() {
        limpar.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
        location.setVisibility(View.INVISIBLE);
        remembrar.setVisibility(View.INVISIBLE);
        desmembrar.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(16, 0, 0, 16);
        undo.setLayoutParams(params);
//        undo.setLayoutParams();

//        incluiAlteracao();
        this.mapCad = true;
    }

    public void setRemembrar(boolean bol){
        if(bol){
            remembrar.setVisibility(View.VISIBLE);
        }else{
            remembrar.setVisibility(View.INVISIBLE);
        }
    }

    public void setDesmembrar(boolean bol){
        if(bol){
            desmembrar.setVisibility(View.VISIBLE);
        }else{
            desmembrar.setVisibility(View.INVISIBLE);
        }

    }

    private void atualizaLocationAtual(double lat, double lon) {
        locationAtual.setPosition(new GeoPoint(lat, lon));
        map.invalidate();
    }

    @SuppressLint("RestrictedApi")
    private void inicializaParamPos() {

        zoomHabilitado = true;
        locationAtual = new Marker(map);
        locationAtual.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        locationAtual.setIcon(getResources().getDrawable(R.drawable.gps_fixed_white_48dp));


        request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(5000);
        client = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if (location != null) {
                        Log.i("LOG", "Latitude novo:" + location.getLatitude());
                        Log.i("LOG", "Longitude novo:" + location.getLongitude());
                        atualizaLocationAtual(location.getLatitude(), location.getLongitude());
                        if (!changePos && location.getLatitude() != 0) {
                            map.getController().setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                            changePos = true;
                        }
                    }
                }
            }
        };
        startGps = false;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        client.requestLocationUpdates(request,
                locationCallback,
                Looper.getMainLooper());
        map.getOverlayManager().add(locationAtual);
    }

    private void stopLocationUpdates() {
        client.removeLocationUpdates(locationCallback);
        map.getOverlayManager().remove(locationAtual);
    }

    private void iniciaFloatingButtons(){
        fab = view.findViewById(R.id.fab);
        save = view.findViewById(R.id.save);
        limpar = view.findViewById(R.id.limpar);
        undo = view.findViewById(R.id.undo);
        location = view.findViewById(R.id.location);
        remembrar = view.findViewById(R.id.remembrar);
        desmembrar = view.findViewById(R.id.desmembrar);
        save.hide();
        limpar.hide();
        undo.hide();

        fab.setColorFilter(Color.parseColor("#FFFFFF"));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alteraEstadoPintura();
            }
        });



        //Botão que salva o poligono no banco de dados
        save.setColorFilter(Color.parseColor("#FFFFFF"));
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(geoPoints.size()>2){
//                    save.setRippleColor(Color.parseColor("#FFFFB300"));
                    salvaPoligono();
                    buffer.clear();
                    if(pintar){
                        alteraEstadoPintura();
                    }
                    if(!croqui){
                        comunicator.salvaPoligono(poligonoAtual.getJTSPolygon(), Integer.parseInt(poligonoAtual.getId()));
                    }else{
                        camadaAtual.addObjeto(poligonoAtual);
                        map.getOverlayManager().add(poligonoAtual);
//                        atualizaInicial();
                    }
                }else{
                    Toast.makeText(map.getContext(), "Necessário geometria com minimo de 3 pontos " , Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Botão que limpa o poligono da tela
        limpar.setColorFilter(Color.parseColor("#FFFFFF"));
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                limpar.setRippleColor(Color.parseColor("#FFFFB300"));
                geoPoints.clear();
                atualizaMapa();
            }
        });

        //Botão que volta o poligono em edição para o estado anterior
        undo.setColorFilter(Color.parseColor("#FFFFFF"));
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                undo.setRippleColor(Color.parseColor("#FFFFB300"));
                undo();

            }
        });


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startGps){
                    startGps = false;
                    changePos = false;
                    location.setColorFilter(Color.parseColor("#FFFFFF"));
                    stopLocationUpdates();
                }else{
                    startGps = true;
                    location.setColorFilter(Color.parseColor("#FFFFB300"));
                    startLocationUpdates();
                }
            }
        });

        remembrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiselecao){
                    if(lstSelecionados.size()>1){
                        remembrar();
                    }else{
                        limpaLstSelecionados();
                        limparSelecao();
                        map.invalidate();
                    }
                    remembrar.setColorFilter(Color.parseColor("#FFFFFF"));
                    multiselecao = false;
                }else{
                    limparSelecao();
                    remembrar.setColorFilter(Color.parseColor("#FFFFB300"));
                    multiselecao = true;
                }
            }
        });

        desmembrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(desmembramento){
                    if(poligonoAtual!=null) {
                        desmembrar();

                    }
                    linePoints.clear();
                    map.getOverlays().remove(evDesmembrar);
                    map.getOverlayManager().removeAll(overlaysDesmembrar);
                    overlaysDesmembrar.clear();
                    map.invalidate();
                    desmembramento = false;
                }else{
                    desmembramento = true;
                    linePoints.clear();
                    map.getOverlays().add(evDesmembrar);
                    map.invalidate();
                }
                InfoWindow.closeAllInfoWindowsOn(map);
//                    comunicator.desmembrar(poligonoAtual.getJTSPolygon(),Integer.parseInt(poligonoAtual.getId()));

            }
        });

    }


    private void limpaLstSelecionados(){

        for (int i=0; i < lstSelecionados.size();i++){
            lstSelecionados.get(i).setStrokeColor(Color.rgb( 0, 0, 255));
            lstSelecionados.remove(i);
        }
    }




    // CLASSE PARA RECEBER DOUBLE TAP E NÃO REALIZAR NADA
    public class OverlayZoom extends Overlay{

        public OverlayZoom(Context ctx) {
            super(ctx);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void draw(Canvas arg0, MapView arg1, boolean arg2) {

        }

        @Override
        public boolean onDoubleTap(MotionEvent e, MapView mapView) {
            //here return true means that I handled double tap event no
            //one need to do anything for this event

            //if you do not do anything here double tap will be disable.
            if (zoomHabilitado){
                return false;
            }else{
                return true;
            }
        }
    }



    private Geometry criaLinhaJTS(Polyline line){
        GeometryFactory factory = new GeometryFactory();

        List<GeoPoint> ringOriginal = line.getPoints();
        Coordinate[] cordinate = new Coordinate[ringOriginal.size()];
        for (int i = 0 ; i < ringOriginal.size(); i++){
            cordinate[i] = new Coordinate(ringOriginal.get(i).getLongitude(),ringOriginal.get(i).getLatitude());
        }

        LineString lr = factory.createLineString(cordinate);
        lr.setSRID(4326);
        return lr;
    }

    public boolean isCalcEnvelope() {
        return calcEnvelope;
    }

    public void setCalcEnvelope(boolean calcEnvelope) {
        this.calcEnvelope = calcEnvelope;
    }


    public void setCroqui(boolean croqui){
        this.croqui = croqui;
    }

    public void setCroquiOptions(List<String> options){
        this.croquiOptions = options;
    }

    public Camada getCamadaAtual(){
        return this.camadaAtual;
    }

    public void setCamadaFundo(Camada camadaFundo){
        this.camadaFundo = camadaFundo;

        if(calcEnvelope){
            map.getController().setCenter(calculaEnvelopeCamada(camadaFundo).centroidAprox());
            setZoom(calculaEnvelopeCamada(camadaFundo).areaQuadradoEnvelope() );
        }

        atualizaInicial();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        client.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            //obtém a última localização conhecida
                            location_origem = task.getResult();

                        } else {
                            Toast.makeText(context,"falha ao capturar localização origem",Toast.LENGTH_SHORT);
                            //Não há localização conhecida ou houve uma excepção
                            //A excepção pode ser obtida com task.getException()
                        }
                    }
                });
    }

}