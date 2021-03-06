﻿using System.Collections.Generic;

namespace DataLayer
{
    public class Game
    {
        public int Id { get; set; }
        public int GameCode { get; set; }
        public int TijdsDuur { get; set; }
        public bool hasStarted { get; set; }
        public double millisStarted { get; set; }
        public List<Team> Teams { get; set; }
        public List<GameDoelen> GameDoelen { get; set; }

    }
}

