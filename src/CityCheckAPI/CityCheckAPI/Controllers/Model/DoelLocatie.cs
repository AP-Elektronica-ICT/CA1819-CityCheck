using CityCheckAPI.Controllers.Model;
using System.Collections.Generic;

namespace Model
{
    public class DoelLocatie
    {
        public int Id { get; set; }
        //public Locatie locatie { get; set; }
        public string Titel { get; set; }
        public List<Vraag> vragen { get; set; }

    }
}

