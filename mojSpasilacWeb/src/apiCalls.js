import axios from '@/axios';

export function login(username, password){
  let params = new URLSearchParams();
  params.append('username', username );
  params.append('password', password );
  return axios.post('/korisnici/login', params)
}