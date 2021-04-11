import axios from '@/axios';

export function login(username, password){
  let params = new URLSearchParams();
  params.append('username', username );
  params.append('password', password );
  return axios.post('/korisnici/login', params)
}

export function getPrijave(){
  //console.log(localStorage.getItem('token'))
  return axios.get('/prijave',{headers: {'Authorization': "Bearer "+ localStorage.getItem('token')}});
}

export function getVozila(){
  //console.log(localStorage.getItem('token'))
  return axios.get('/vozila',{headers: {'Authorization': "Bearer "+ localStorage.getItem('token')}});
}

export function dodeliVoziluPrijavu(id_vozila,id_prijave){
  let params = new URLSearchParams();
  params.append('id_vozila', id_vozila );
  params.append('id_prijave', id_prijave );
  return axios.post('/vozila/dodeli', params,{headers: {'Authorization': "Bearer "+ localStorage.getItem('token')}} )
}