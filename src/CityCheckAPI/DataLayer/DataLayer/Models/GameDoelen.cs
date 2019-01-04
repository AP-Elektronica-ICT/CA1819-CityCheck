using System.Collections.Generic;

namespace DataLayer
{
    public class GameDoelen
    {
        public int Id { get; set; }
        public DoelLocatie Doel { get; set; }
        public bool Claimed { get; set; }

    }
}

