const pool = require('./connection');
const prijave = require('../database/tabela-prijave');
const tabela='vozila';
const tabela_korisnici = 'korisnici';
const tabela_prijave = 'prijave';

const vozila = {
  insert: async function(vozilo){
    let conn;
    try {
      conn = await pool.getConnection();
      const res = await conn.query("INSERT INTO "+tabela+
      " (id_korisnika, kapacitet,lokacija_vozila_x,lokacija_vozila_y) VALUES (?,?,?,?)", [vozilo.id_korisnika,vozilo.kapacitet,vozilo.lokacija_vozila_x,vozilo.lokacija_vozila_y]);
      console.log(res); 
      if(res.affectedRows==0)
        throw new Error('Nije uspelo upisivanje u bazu');
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  },
  svaVozila: async function() {
    let conn;
    try {
      conn = await pool.getConnection();
      const lista_prijava = await conn.query("SELECT * from "+
      tabela_prijave + " WHERE aktivna_prijava = 1");
      const lista_vozila = await conn.query("SELECT vozila.*, username, id_prijave from "+
      tabela_korisnici + " JOIN " + tabela + " USING (id_korisnika)"
      +" JOIN vozila_prijave USING (id_vozila)");
      for(let i = 0; i < lista_vozila.length; i++)
      {
          for(let j = 0; j < lista_prijava.length; j++)
          {
            if(lista_vozila[i].id_prijave == lista_prijava[j].id_prijave)
            {
                lista_vozila[i].prijava = lista_prijava[j];
                break;
            }
          }
      }
      //lista_vozila[0].prijava = lista_prijava[0];
      //for(let i = 0; i < )
      /*for (let v of lista_vozila) {
        for (let l of lista_vozila) {
            if(v.id_prijave == l.id_prijave) v.prijava=l;
          }
      }*/

      /*lista_vozila.forEach(v => {
          lista_prijava.forEach(l => {
              if(v.id_prijave == l.id_prijave)
              {
                  //v.prijava = l;
                  break;
              }
          });
      });*/
      conn.end();
      return lista_vozila;
    } catch (err) {
      throw err;
    }
  }
}

module.exports=vozila;