using System.Collections.Generic;

namespace DataLayer
{
    public class DoelLocatie
    {
        public int Id { get; set; }
        //public Locatie locatie { get; set; }
        public string Titel { get; set; }
        public Locatie locatie {get; set; }
        public List<Vraag> Vragen { get; set; }

    }
}

