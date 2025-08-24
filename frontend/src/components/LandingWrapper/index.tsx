import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAppContext } from "../../contexts";

const LandingWrapper = () => {
  const navigate = useNavigate();
  const { subjects, loading } = useAppContext();

  useEffect(() => {
    if (loading) return; // don’t redirect while still loading

    if (subjects.length === 0) {
      navigate("/subjects");
    } else {
      navigate("/home");
    }
  }, [subjects, loading, navigate]);

  // Optional: render something while loading
  if (loading) {
    return <div>Loading...</div>;
  }

  return null;
};

export default LandingWrapper;
