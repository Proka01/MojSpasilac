const pool = require('./connection');
const tabela='osobe';

const osobe={
  insert: async function(id_prijave,osobe){
    let conn;
    try {
      conn = await pool.getConnection();
      let q="INSERT INTO "+tabela+" (id_prijave,broj_godina,id_zdravstvenog_stanja) VALUES ";
      let params=[];
      for(let osoba of osobe){
        q+="(?,?,?),";
        params.push(id_prijave,osoba.broj_godina,osoba.id_zdravstvenog_stanja);
      }
      q=q.substring(0,q.length-1);
      const res = await conn.query(q,params);
      if(res.affectedRows==0)
        throw new Error('Nije uspelo upisivanje u bazu');
      conn.end();
      return res;
    } catch (err) {
      throw err;
    }
  }
}

module.exports=osobe;